import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.IpOrWebOrSubDomainToVulDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;
import testnet.common.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 脚本名称：nuclei漏洞扫描
 * 适用资产：WEB、子域名、ip
 * 配置：
 * command: '/testnet-client/xpoc -t %s -o %s'
 * 结果处理类名: ipOrWebOrSubDomainToVulProcessor
 */

public class Xpoc implements JaninoCommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        try {
            ILiteFlowMessageSendService sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String resultPath = taskExecuteMessage.getResultPath() + "xpoc_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            String asset = "";
            switch (taskExecuteMessage.getAssetType()) {
                case "ip":
                    asset = instanceParams.getString("ip");
                    break;
                case "web":
                    asset = instanceParams.getString("webUrl");
                    break;
                case "sub_domain":
                    asset = instanceParams.getString("subDomain");
                    break;
            }
            command = String.format(command, asset, resultPath);
            sendService.INFO("开始执行Xpoc漏洞扫描,命令是：{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                    JSONArray jsonArray = JSON.parseArray(content);
                    if (jsonArray != null) {
                        IpOrWebOrSubDomainToVulDTO ipOrWebOrSubDomainToVulDTO = new IpOrWebOrSubDomainToVulDTO();
                        List<IpOrWebOrSubDomainToVulDTO.AssetVul> assetVulList = new ArrayList<>();
                        for (Object o : jsonArray) {
                            JSONObject json = (JSONObject) o;
                            if (json.getString("type") != null && json.getString("type").equals("*event.Vulnerability")) {
                                sendService.INFO("Xpoc漏洞扫描发现漏洞，结果是：" + json);
                                IpOrWebOrSubDomainToVulDTO.AssetVul assetVul = new IpOrWebOrSubDomainToVulDTO.AssetVul();
                                assetVul.setVulName(json.getJSONObject("value").getString("name"));
                                assetVul.setVulDesc(json.getJSONObject("value").getString("links"));
                                assetVul.setPayload(JSONObject.toJSONString(json.getJSONObject("value").getJSONObject("proof").getJSONArray("steps")));
                                assetVul.setSeverity("high");
                                assetVulList.add(assetVul);
                            }
                        }
                        ipOrWebOrSubDomainToVulDTO.setAssetVulList(assetVulList);
                        sendService.sendResult(ipOrWebOrSubDomainToVulDTO);
                    }
                } else {
                    sendService.INFO("Xpoc漏洞扫描执行成功，结果文件不存在，可能扫描结果为空！");
                }
            } else {
                sendService.ERROR("Xpoc漏洞扫描执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}