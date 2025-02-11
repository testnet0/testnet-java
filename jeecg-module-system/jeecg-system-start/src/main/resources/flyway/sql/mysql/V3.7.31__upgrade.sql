# 修复端口复测错误
UPDATE lite_flow_script set script_data ='import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang3.StringUtils;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.dto.IpOrSubDomainToPortDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;
import testnet.common.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 脚本名称：Naabu 端口扫描
 * 适用资产：子域名、IP
 * 配置：
 * command: ''naabu -no-stdin -c 200 -json -top-ports 100 -host %s -o %s''
 * 结果处理类名: ipOrSubDomainToPortProcessor
 */
public class Naabu implements CommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        // 获取的是chain初始化的参数
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService messageSendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String resultPath = taskExecuteMessage.getResultPath() + "naabu_" + UUID.randomUUID() + ".json";
            messageSendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            String asset = "";
            switch (taskExecuteMessage.getAssetType()) {
                case "ip":
                    asset = instanceParams.getString("ip");
                    command = String.format(command, asset, resultPath);
                    break;
                case "sub_domain":
                    asset = instanceParams.getString("subDomain");
                    command = String.format(command, asset, resultPath);
                    break;
                case "port":
                    asset = instanceParams.getString("ip_dictText") + ":" + instanceParams.getString("port");
                    command = String.format(command, instanceParams.getString("ip_dictText"), resultPath, instanceParams.getString("port"));
            }
            messageSendService.INFO("开始执行Nabbu端口扫描,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (taskExecuteMessage.getAssetType().equals("port")) {
                    String readFile = FileUtils.readFile(resultPath);
                    messageSendService.INFO("Nabbu端口扫描执行结果:{}", readFile);
                    AssetUpdateDTO assetUpdateDTO = new AssetUpdateDTO();
                    if (StringUtils.isNotBlank(readFile)) {
                        instanceParams.put("isOpen", "Y");
                    } else {
                        instanceParams.put("isOpen", "N");
                    }
                    assetUpdateDTO.setData(instanceParams.toString());
                    messageSendService.sendResult(assetUpdateDTO);
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(Paths.get(resultPath).toFile()));
                    String line;
                    IpOrSubDomainToPortDTO dto = new IpOrSubDomainToPortDTO();
                    List<IpOrSubDomainToPortDTO.Port> portList = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        // 处理每一行内容
                        JSONObject jsonObject = JSONObject.parseObject(line);
                        messageSendService.INFO("Nabbu端口扫描执行结果:{}", jsonObject);
                        if (jsonObject != null) {
                            IpOrSubDomainToPortDTO.Port port = new IpOrSubDomainToPortDTO.Port();
                            port.setPort(jsonObject.getInteger("port"));
                            port.setProtocol(jsonObject.getString("protocol"));
                            port.setIp(jsonObject.getString("ip"));
                            port.setHost(jsonObject.getString("host"));
                            portList.add(port);
                        }
                    }
                    dto.setPortList(portList);
                    messageSendService.sendResult(dto);
                }
            } else if (result.getExitCode() == 1) {
                messageSendService.INFO("资产: {} 端口扫描完成，没有端口开放", asset);
                if (taskExecuteMessage.getAssetType().equals("port")) {
                    AssetUpdateDTO assetUpdateDTO = new AssetUpdateDTO();
                    instanceParams.put("isOpen", "N");
                    assetUpdateDTO.setData(instanceParams.toString());
                    messageSendService.sendResult(assetUpdateDTO);
                }
            } else {
                messageSendService.ERROR("Nabbu端口扫描执行失败,错误信息是:{}", result.getExitCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}' WHERE id = '1773681041704804353';

UPDATE  lite_flow_chain set config = 'command: ''naabu -no-stdin -c 200 -json -host %s -o %s -p %s''' where id = '1880156749014491138';

UPDATE client_config set config = 'command: ''naabu -no-stdin -c 200 -json -host %s -o %s -p %s''' where chain_id = '1880156749014491138';
