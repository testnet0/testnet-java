import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetApiToApiDTO;
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
 * 脚本名称：Katana爬虫扫描扫描
 * 适用资产：API
 * command: 'katana -u %s -o %s -j -duc -or -ob -silent -jc'
 * 结果处理类名: ApiToApiProcessor
 */

public class Katana implements CommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            String command = config.getString("command");
            String resultPath = taskExecuteMessage.getResultPath() + "katana_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            JSONObject jsonObject = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String webUrl = jsonObject.getString("absolutePath");
            command = String.format(command, webUrl, resultPath);
            sendService.INFO("开始执行Katana工具扫描,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    BufferedReader reader = new BufferedReader(new FileReader(Paths.get(resultPath).toFile()));
                    String line;
                    List<AssetApiToApiDTO.AssetApiDTO> assetApiDTOList = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        // 处理每一行内容
                        JSONObject jsonResult = JSONObject.parseObject(line);
                        sendService.INFO("Katana扫描执行结果:{}", jsonResult);
                        if (jsonResult != null) {
                            JSONObject request = jsonResult.getJSONObject("request");
                            if (request != null) {
                                AssetApiToApiDTO.AssetApiDTO assetApiDTO = new AssetApiToApiDTO.AssetApiDTO();
                                assetApiDTO.setHttpMethod(request.getString("method"));
                                assetApiDTO.setAbsolutePath(request.getString("endpoint"));
                                JSONObject response = jsonResult.getJSONObject("response");
                                if (response != null) {
                                    assetApiDTO.setStatusCode(response.getInteger("status_code"));
                                    JSONObject headers = response.getJSONObject("headers");
                                    if (headers != null) {
                                        if (headers.containsKey("content_type")) {
                                            assetApiDTO.setContentType(headers.getString("content_type"));
                                        }
                                        if (headers.containsKey("content_length")) {
                                            assetApiDTO.setContentLength(Integer.valueOf(headers.getString("content_length")));
                                        }
                                    }
                                }
                                assetApiDTOList.add(assetApiDTO);
                            }
                        }
                    }
                    AssetApiToApiDTO assetApiToApiDTO = new AssetApiToApiDTO();
                    assetApiToApiDTO.setAssetApiDTOList(assetApiDTOList);
                    sendService.sendResult(assetApiToApiDTO);
                } else {
                    sendService.INFO("Katana工具扫描执行成功，结果文件为空");
                }
            } else {
                sendService.ERROR("Katana工具扫描执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}