import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang.StringUtils;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.entity.liteflow.ClientToolVersion;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;
import testnet.common.utils.FileUtils;

import java.io.IOException;
import java.util.UUID;

public class ToolsInstall implements JaninoCommonScriptBody {

    private final ILiteFlowMessageSendService sendService;

    public ToolsInstall() {
        this.sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
    }

    @Override
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        sendService.setTaskId(taskExecuteMessage.getTaskId());
        JSONObject params = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        if (params == null) {
            sendService.ERROR("工具安装失败！参数为空");
            return null;
        }
        String installCommand = params.getString("installCommand");
        String checkVersionCommand = params.getString("versionCheckCommand");
        try {
            boolean isInstalled = checkToolVersion(checkVersionCommand);
            if (!isInstalled) {
                executeCommand(installCommand);
                checkToolVersion(checkVersionCommand);
            } else {
                sendService.INFO("工具已存在，跳过安装。");
            }
        } catch (Exception e) {
            sendService.ERROR("工具安装过程中发生错误：" + e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    private boolean checkToolVersion(String checkVersionCommand) {
        String versionFileName = generateFileName();
        FileUtils.createFileAndWrite(versionFileName, checkVersionCommand);

        if (!FileUtils.fileExists(versionFileName)) {
            sendService.ERROR("检查版本文件创建失败");
            return false;
        }
        CommandUtils.CommandResult versionResult = null;
        try {
            versionResult = CommandUtils.executeCommand("bash " + versionFileName);
            FileUtils.deleteFile(versionFileName);
            sendService.INFO("开始检测工具版本" + "，命令是：\n" + checkVersionCommand);
            if (versionResult.getExitCode() == 0) {
                if (StringUtils.isNotBlank(versionResult.getOutput())) {
                    sendService.INFO("工具安装成功，版本是：" + versionResult.getOutput());
                    ClientToolVersion clientToolVersion = new ClientToolVersion();
                    clientToolVersion.setToolVersion(versionResult.getOutput());
                    sendService.sendResult(clientToolVersion);
                    return true;
                } else {
                    sendService.INFO("工具未安装");
                    return false;
                }
            } else {
                sendService.ERROR("工具版本检查失败，状态码是：" + versionResult.getExitCode() + ",错误信息：" + versionResult.getOutput());
                return false;
            }
        } catch (Exception e) {
            if (versionResult != null) {
                sendService.ERROR("工具版本检查失败，错误信息：" + e.getMessage() + "输出信息：", versionResult.getOutput());
            }
            return false;
        }
    }

    private void executeCommand(String command) throws IOException, InterruptedException {
        String fileName = generateFileName();
        FileUtils.createFileAndWrite(fileName, command);

        if (!FileUtils.fileExists(fileName)) {
            sendService.ERROR("文件创建失败：" + fileName);
            return;
        }
        sendService.INFO("开始安装工具" + "，命令是：\n" + command);
        CommandUtils.CommandResult result = CommandUtils.executeCommand("bash " + fileName);
        FileUtils.deleteFile(fileName);

        if (result.getExitCode() == 0) {
            sendService.INFO("工具安装成功，开始检查版本");
        } else {
            sendService.ERROR("工具安装失败,状态码是：" + result.getExitCode() + ",错误信息：" + result.getOutput());
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".sh";
    }
}