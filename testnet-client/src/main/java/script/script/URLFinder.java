package script.script;

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
 * 脚本名称：URLFinder路径扫描
 * 适用资产：API
 * command: 'URLFinder -u %s -o %s'
 * 结果处理类名: ApiToApiProcessor
 */

public class URLFinder implements CommonScriptBody {
    private List<AssetApiToApiDTO.AssetApiDTO> assetApiDTOList = new ArrayList<>();

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            String command = config.getString("command");
            String resultPath = taskExecuteMessage.getResultPath() + "urlfinder_" + UUID.randomUUID() + ".json";
            sendService.INFO("结果保存路径:{}", resultPath);
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String webUrl = instanceParams.getString("absolutePath");
            command = String.format(command, webUrl, resultPath);
            sendService.INFO("开始执行URLFinder路径扫描,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                if (FileUtils.fileExists(resultPath)) {
                    String content = new String(Files.readAllBytes(Paths.get(resultPath)));
                    // sendService.INFO("URLFinder工具扫描执行结果:{}", content);
                    JSONObject jsonObject = JSONObject.parseObject(content);
                    JSONArray url = jsonObject.getJSONArray("url");
                    JSONArray urlOther = jsonObject.getJSONArray("urlOther");
                    processResult(url);
                    processResult(urlOther);
                    if (!assetApiDTOList.isEmpty()) {
                        AssetApiToApiDTO assetApiToApiDTO = new AssetApiToApiDTO();
                        assetApiToApiDTO.setAssetApiDTOList(assetApiDTOList);
                        sendService.sendResult(assetApiToApiDTO);
                    }
                } else {
                    sendService.INFO("URLFinder工具扫描执行成功，结果文件为空");
                }
            } else {
                sendService.ERROR("URLFinder工具扫描执行失败,状态码是:{},输出信息:{}", result.getExitCode(), result.getOutput());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void processResult(JSONArray urlOther) {
        if (urlOther != null) {
            for (Object o : urlOther) {
                JSONObject json = (JSONObject) o;
                AssetApiToApiDTO.AssetApiDTO assetApiDTO = new AssetApiToApiDTO.AssetApiDTO();
                assetApiDTO.setHttpMethod("GET");
                assetApiDTO.setAbsolutePath(json.getString("Url"));
                assetApiDTO.setTitle(json.getString("Title"));
                assetApiDTOList.add(assetApiDTO);
            }
        }
    }
}