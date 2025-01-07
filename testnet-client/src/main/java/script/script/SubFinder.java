import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.DomainToSubdomainsAndIpsDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 脚本名称：SubFinder子域名扫描
 * 适用资产：域名
 * 配置：
 * resultPath: '/testnet-client/results/subfinder-%s.json'
 * command: 'subfinder -d %s -duc -oJ -o %s'
 * 结果处理类名: domainToSubDomainAndIPProcessor
 */
public class SubFinder implements CommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String domain = instanceParams.getString("domain");
            String resultPath = taskExecuteMessage.getResultPath() + "subfinder_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            command = String.format(command, domain, resultPath);
            sendService.INFO("开始执行SubFinder,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            sendService.INFO("开始解析SubFinder结果");
            if (result.getExitCode() == 0) {
                sendService.INFO("执行成功,输出结果: {}", result.getOutput());
                BufferedReader reader = new BufferedReader(new FileReader(Paths.get(resultPath).toFile()));
                String line;
                DomainToSubdomainsAndIpsDTO domainToSubdomainsAndIpsDTO = new DomainToSubdomainsAndIpsDTO();
                List<DomainToSubdomainsAndIpsDTO.SudDomain> subDomainList = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    // 处理每一行内容
                    JSONObject jsonObject = JSONObject.parseObject(line);
                    sendService.INFO("SubFinder子域名扫描执行结果:{}", jsonObject);
                    if (jsonObject != null) {
                        DomainToSubdomainsAndIpsDTO.SudDomain sudDomain = new DomainToSubdomainsAndIpsDTO.SudDomain();
                        sudDomain.setSubDomain(jsonObject.getString("host"));
                        sudDomain.setSource("SubFinder-" + ":" + jsonObject.getString("source"));
                        subDomainList.add(sudDomain);
                    }
                }
                domainToSubdomainsAndIpsDTO.setSubDomainList(subDomainList);
                sendService.sendResult(domainToSubdomainsAndIpsDTO);
            } else {
                sendService.ERROR("执行失败,输出结果: {}", result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}