import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.IpOrSubDomainToPortDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;
import testnet.common.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 脚本名称：Masscan 端口扫描
 * 适用资产：子域名、IP
 * 配置：
 * command: 'masscan -p1-65535 %s -oJ %s'
 * 结果处理类名: ipOrSubDomainToPortProcessor
 */
public class Masscan implements JaninoCommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        // 获取的是chain初始化的参数
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService messageSendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());

            String command = config.getString("command");
            List<String> assetList = new ArrayList<>();
            switch (taskExecuteMessage.getAssetType()) {
                case "ip":
                    assetList.add(instanceParams.getString("ip"));
                    break;
                case "sub_domain":
                    JSONArray ipList = instanceParams.getJSONArray("ipList");
                    for (int i = 0; i < ipList.size(); i++) {
                        String ip = ipList.getJSONObject(i).getString("ip");
                        assetList.add(ip);
                    }
                    break;
            }
            for (int k = 0; k < assetList.size(); k++) {
                String asset = (String) assetList.get(k);
                String resultPath = taskExecuteMessage.getResultPath() + "masscan_" + UUID.randomUUID() + ".json";
                messageSendService.INFO("结果保存路径:{}", resultPath);
                command = String.format(command, asset, resultPath);
                messageSendService.INFO("开始执行Masscan端口扫描,命令是:{}", command);
                CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
                if (result.getExitCode() == 0) {
                    if (FileUtils.fileExists(resultPath)) {
                        String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                        JSONArray jsonArray = JSONArray.parseArray(content);
                        if (jsonArray != null && !jsonArray.isEmpty()) {
                            IpOrSubDomainToPortDTO dto = new IpOrSubDomainToPortDTO();
                            List<IpOrSubDomainToPortDTO.Port> portList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray ports = jsonObject.getJSONArray("ports");
                                for (int j = 0; j < ports.size(); j++) {
                                    JSONObject portObject = ports.getJSONObject(j);
                                    IpOrSubDomainToPortDTO.Port port = new IpOrSubDomainToPortDTO.Port();
                                    port.setPort(portObject.getInteger("port"));
                                    port.setProtocol(portObject.getString("proto"));
                                    port.setIp(jsonObject.getString("ip"));
                                    portList.add(port);
                                }
                            }
                            dto.setPortList(portList);
                            messageSendService.sendResult(dto);
                        } else {
                            messageSendService.INFO("资产: {} 端口扫描完成，结果为空", asset);
                        }
                    } else {
                        messageSendService.INFO("资产: {} 端口扫描完成，结果文件不存在", asset);
                    }
                } else if (result.getExitCode() == 1) {
                    messageSendService.INFO("资产: {} 端口扫描完成，没有端口开放", asset);
                } else {
                    messageSendService.ERROR("Masscan端口扫描执行失败,错误信息是:{}", result.getExitCode());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
