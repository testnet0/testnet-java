import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;
import testnet.common.utils.FileUtils;
import testnet.common.utils.ObjectBase64Decoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 脚本名称：Xapp指纹探测
 * 适用资产：WEB
 * 配置：
 * command: '/testnet-client/xapp -t %s -o %s --silent'
 * 结果处理类名: assetUpdateProcessor
 */

public class Xapp implements JaninoCommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        try {
            ILiteFlowMessageSendService sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String command = config.getString("command");
            String asset = instanceParams.getString("webUrl");
            String resultPath = taskExecuteMessage.getResultPath() + "xapp_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            command = String.format(command, asset, resultPath);
            sendService.INFO("开始执行Xapp指纹探测,命令是：{}", command);
            // 超时60秒
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command, 60);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                    JSONArray jsonArray = JSON.parseArray(content);
                    if (jsonArray != null) {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject value = jsonArray.getJSONObject(0).getJSONObject("value");
                        String title = value.getString("title");
                        jsonObject.put("webTitle", title);
                        sendService.INFO("提取到的标题是:{}", title);
                        JSONArray fingerprints = value.getJSONArray("fingerprints");
                        JSONObject httpFlow = value.getJSONObject("httpFlow");
                        if (httpFlow != null) {
                            JSONArray exchanges = httpFlow.getJSONArray("exchanges");
                            if (exchanges != null && !exchanges.isEmpty()) {
                                String requestHeader = exchanges.getJSONObject(0).getJSONObject("request").getString("rawHeader");
                                jsonObject.put("requestHeader", ObjectBase64Decoder.decodeString(requestHeader));
                                String statusCode = exchanges.getJSONObject(0).getJSONObject("response").getString("status");
                                jsonObject.put("statusCode", statusCode.trim());
                                String responseHeader = exchanges.getJSONObject(0).getJSONObject("response").getString("rawHeader");
                                jsonObject.put("responseHeader", ObjectBase64Decoder.decodeString(responseHeader));
                            }
                        }
                        JSONArray tech = new JSONArray();
                        if (fingerprints != null) {
                            for (int i = 0; i < fingerprints.size(); i++) {
                                JSONObject fingerprint = fingerprints.getJSONObject(i);
                                String fingerprintName = fingerprint.getJSONObject("product").getString("name");
                                sendService.INFO("Xapp指纹探测执行成功，获取到的指纹是：{}", fingerprintName);
                                JSONObject tmp = new JSONObject();
                                tmp.put("name", fingerprintName);
                                tech.add(tmp);
                            }
                        }

                        jsonObject.put("portId", instanceParams.getString("portId"));
                        jsonObject.put("domain", instanceParams.getString("domain"));
                        jsonObject.put("projectId", instanceParams.getString("projectId"));
                        jsonObject.put("tech", tech);
                        AssetUpdateDTO assetUpdateDTO = new AssetUpdateDTO();
                        assetUpdateDTO.setData(jsonObject.toJSONString());
                        sendService.sendResult(assetUpdateDTO);
                    }
                } else {
                    sendService.INFO("Xapp指纹探测执行成功，结果文件不存在，可能扫描结果为空！");
                }
            } else {
                sendService.ERROR("Xapp指纹探测执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
