package script.script;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang.StringUtils;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脚本名称：TideFinger_Go指纹探测
 * 适用资产：WEB
 * 配置：
 * command: '/testnet-client/TideFinger -u %s -np -nobr -nopoc'
 * 结果处理类名: assetUpdateProcessor
 */

public class TideFinger_Go implements CommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            String command = config.getString("command");
            String asset = instanceParams.getString("webUrl");
            command = String.format(command, asset);
            sendService.INFO("开始执行TideFinger,命令是：{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                sendService.INFO("TideFinger执行成功，结果是:{}", result.getOutput());
                Pattern pattern = Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}] \\[\\+] \\[(TCP/HTTP|TLS/HTTPS)] \\[(\\d{3})] ((\\[.*?])+?) .*? \\[(.*?)]");
                Matcher matcher = pattern.matcher(result.getOutput());
                while (matcher.find()) {
                    String statusCode = matcher.group(2);
                    String fingerprint = matcher.group(3);
                    String title = matcher.group(5);
                    sendService.INFO("提取到的指纹是:{}", fingerprint);
                    sendService.INFO("提取到的标题是:{}", title);
                    sendService.INFO("提取到的状态码是:{}", statusCode);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("portId", instanceParams.getString("portId"));
                    jsonObject.put("domain", instanceParams.getString("domain"));
                    jsonObject.put("projectId", instanceParams.getString("projectId"));
                    if (StringUtils.isNotBlank(fingerprint)) {
                        JSONArray versions = new JSONArray();
                        String[] parts = fingerprint.split("]\\["); // 用特定的分隔符来拆分字符串
                        for (String part : parts) {
                            part = part.replace("[", "").replace("]", "");
                            if (!part.trim().isEmpty()) {
                                // 进一步拆分每个部分以获取名称和版本
                                String[] nameAndVersion = part.split(" ");
                                JSONObject softwareInfo = new JSONObject();
                                softwareInfo.put("name", nameAndVersion[0]); // 软件名称
                                if (nameAndVersion.length > 1) {
                                    softwareInfo.put("version", nameAndVersion[1]); // 软件版本
                                }
                                versions.add(softwareInfo);
                            }
                        }
                        jsonObject.put("tech", versions);
                    }
                    jsonObject.put("title", title);
                    jsonObject.put("statusCode", statusCode);

                    AssetUpdateDTO assetUpdateDTO = new AssetUpdateDTO();
                    assetUpdateDTO.setData(jsonObject.toJSONString());
                    sendService.sendResult(assetUpdateDTO);
                }
            } else {
                sendService.ERROR("TideFinger执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
