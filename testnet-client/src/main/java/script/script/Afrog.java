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
 * 脚本名称：Afrog漏洞扫描
 * 适用资产：WEB、子域名、ip
 * 配置：
 * command: 'afrog  -t %s -json %s'
 * 结果处理类名: ipOrWebOrSubDomainToVulProcessor
 */

public class Afrog implements JaninoCommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        ILiteFlowMessageSendService sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
        try {
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String resultPath = taskExecuteMessage.getResultPath() + "afrog_" + UUID.randomUUID() + ".json";
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
            sendService.INFO("开始执行Afrog漏洞扫描,命令是：{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                    JSONArray jsonArray = JSON.parseArray(content);
                    if (jsonArray != null) {
                        sendService.INFO("Afrog漏洞扫描成功，结果是：" + content);
                        IpOrWebOrSubDomainToVulDTO ipOrWebOrSubDomainToVulDTO = getIpOrWebOrSubDomainToVulDTO(jsonArray);
                        sendService.sendResult(ipOrWebOrSubDomainToVulDTO);
                    }
                } else {
                    sendService.INFO("Afrog漏洞扫描执行成功，结果文件不存在，可能扫描结果为空！");
                }
            } else {
                sendService.ERROR("Afrog漏洞扫描执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            sendService.ERROR("Afrog漏洞扫描执行失败,错误信息:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    private IpOrWebOrSubDomainToVulDTO getIpOrWebOrSubDomainToVulDTO(JSONArray jsonArray) {
        IpOrWebOrSubDomainToVulDTO ipOrWebOrSubDomainToVulDTO = new IpOrWebOrSubDomainToVulDTO();
        List<IpOrWebOrSubDomainToVulDTO.AssetVul> assetVulList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            IpOrWebOrSubDomainToVulDTO.AssetVul assetVul = new IpOrWebOrSubDomainToVulDTO.AssetVul();
            JSONObject info = jsonObject.getJSONObject("pocinfo");
            if (info != null) {
                assetVul.setVulName(info.getString("infoname"));
                assetVul.setSeverity(info.getString("infoseg"));
                assetVul.setVulDesc(info.getString("infodescription"));
            }
            if (jsonObject.getString("request") != null) {
                assetVul.setRequestBody(jsonObject.getString("request"));
            }
            if (jsonObject.getString("response") != null) {
                assetVul.setResponseBody(jsonObject.getString("response"));
            }
            if (jsonObject.getString("fulltarget") != null) {
                assetVul.setVulUrl(jsonObject.getString("fulltarget"));
            }
            assetVulList.add(assetVul);
        }
        ipOrWebOrSubDomainToVulDTO.setAssetVulList(assetVulList);
        return ipOrWebOrSubDomainToVulDTO;
    }

}