import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.DomainToSubdomainsAndIpsDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 脚本名称：OneForAll子域名扫描
 * 适用资产：域名
 * 配置：
 * command: 'python3 /testnet-client/OneForAll/oneforall.py --target %s --req False --fmt json --path %s run'
 * 结果处理类名: domainToSubDomainAndIPProcessor
 */
public class OneForAll implements JaninoCommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        try {
            ILiteFlowMessageSendService sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String domain = instanceParams.getString("domain");
            String resultPath = taskExecuteMessage.getResultPath() + "oneforall_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            command = String.format(command, domain, resultPath);
            sendService.INFO("开始执行OneForAll,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            sendService.INFO("开始解析OneForAll结果");
            if (result.getExitCode() == 0) {
                sendService.INFO("执行成功,输出结果: {}", result.getOutput());
                String jsonContent = new String(Files.readAllBytes(Paths.get(resultPath)));
                JSONArray jsonArray = JSON.parseArray(jsonContent);
                DomainToSubdomainsAndIpsDTO domainToSubdomainsAndIpsDTO = new DomainToSubdomainsAndIpsDTO();
                List<DomainToSubdomainsAndIpsDTO.SudDomain> subDomainList = getSudDomains(jsonArray);
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

    private List<DomainToSubdomainsAndIpsDTO.SudDomain> getSudDomains(JSONArray jsonArray) {
        List<DomainToSubdomainsAndIpsDTO.SudDomain> subDomainList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            DomainToSubdomainsAndIpsDTO.SudDomain sudDomain = new DomainToSubdomainsAndIpsDTO.SudDomain();
            sudDomain.setLevel(jsonObject.getInteger("level"));
            sudDomain.setSubDomain(jsonObject.getString("subdomain"));
            sudDomain.setIpList(jsonObject.getString("ip"));
            sudDomain.setSource("OneForAll-" + ":" + jsonObject.getString("source"));
            subDomainList.add(sudDomain);
        }
        return subDomainList;
    }

}