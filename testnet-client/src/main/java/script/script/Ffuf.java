import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetApiToApiDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;
import testnet.common.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 脚本名称：FFUF敏感目录扫描
 * 适用资产：API
 * command: 'ffuf -u %s/FUZZ -w /path/to/wordlist -mc all -of json -o %s'
 * 结果处理类名: ApiToApiProcessor
 */

public class Ffuf implements CommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            String command = config.getString("command");
            String resultPath = taskExecuteMessage.getResultPath() + "ffuf_search_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            JSONObject jsonObject = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String webUrl = jsonObject.containsKey("absolutePath") ? jsonObject.getString("absolutePath") : jsonObject.getString("webUrl");
            command = String.format(command, webUrl, resultPath);
            sendService.INFO("开始执行FFUF敏感目录扫描,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                    JSONObject resultJson = JSON.parseObject(content);
                    if (resultJson != null) {
                        sendService.INFO("FFUF敏感目录扫描成功，结果是：{}", content);
                        JSONArray results = resultJson.getJSONArray("results");
                        AssetApiToApiDTO assetApiToApiDTO = new AssetApiToApiDTO();
                        if (results != null && !results.isEmpty()) {
                            List<AssetApiToApiDTO.AssetApiDTO> assetApiDTOList = new ArrayList<>();
                            for (int i = 0; i < results.size(); i++) {
                                JSONObject resultItem = results.getJSONObject(i);
                                AssetApiToApiDTO.AssetApiDTO assetApiDTO = new AssetApiToApiDTO.AssetApiDTO();
                                assetApiDTO.setAbsolutePath(resultItem.getString("url"));
                                assetApiDTO.setStatusCode(resultItem.getInteger("status"));
                                assetApiDTO.setContentLength(resultItem.getInteger("length"));
                                assetApiDTO.setContentType(resultItem.getString("content-type"));
                                assetApiDTO.setHttpMethod("GET");
                                assetApiDTOList.add(assetApiDTO);
                            }
                            assetApiToApiDTO.setAssetApiDTOList(assetApiDTOList);
                            sendService.sendResult(assetApiToApiDTO);
                        } else {
                            sendService.INFO("FFUF敏感目录扫描执行成功，结果文件为空");
                        }
                    }
                } else {
                    sendService.INFO("FFUF敏感目录扫描执行成功，结果文件不存在，可能扫描结果为空！");
                }
            } else {
                sendService.ERROR("FFUF敏感目录扫描执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
