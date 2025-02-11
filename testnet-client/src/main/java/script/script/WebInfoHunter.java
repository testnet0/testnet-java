package script.script;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
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
 * 脚本名称：WebInfoHunter敏感信息扫描
 * 适用资产：WEB、子域名、ip
 * 配置：
 * command: 'wih -t %s -J -o %s -r /testnet-client/tools/rules.yml --dc'
 * 结果处理类名: ipOrWebOrSubDomainToVulProcessor
 */

public class WebInfoHunter implements CommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String resultPath = taskExecuteMessage.getResultPath() + "wih_" + UUID.randomUUID() + ".json";
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
            sendService.INFO("开始执行Wih敏感信息扫描,命令是：{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                    JSONObject jsonObject = JSONObject.parseObject(content);
                    JSONArray jsonArray = jsonObject.getJSONArray("records");
                    if (jsonArray != null) {
                        IpOrWebOrSubDomainToVulDTO ipOrWebOrSubDomainToVulDTO = new IpOrWebOrSubDomainToVulDTO();
                        List<IpOrWebOrSubDomainToVulDTO.AssetVul> assetVulList = new ArrayList<>();
                        for (Object o : jsonArray) {
                            JSONObject json = (JSONObject) o;
                            sendService.INFO("wih敏感信息扫描发现漏洞，结果是：" + json);
                            IpOrWebOrSubDomainToVulDTO.AssetVul assetVul = new IpOrWebOrSubDomainToVulDTO.AssetVul();
                            assetVul.setVulName(json.getString("id") + "敏感信息泄漏: " + json.getString("content"));
                            assetVul.setVulDesc("wih发现敏感信息泄漏");
                            assetVul.setVulUrl(json.getString("source"));
                            assetVul.setPayload(json.getString("content"));
                            assetVul.setSeverity("high");
                            assetVulList.add(assetVul);
                        }
                        ipOrWebOrSubDomainToVulDTO.setAssetVulList(assetVulList);
                        sendService.sendResult(ipOrWebOrSubDomainToVulDTO);
                    }
                } else {
                    sendService.INFO("wih敏感信息扫描执行成功，结果文件不存在，可能扫描结果为空！");
                }
            } else {
                sendService.ERROR("wih敏感信息扫描执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}