import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang.StringUtils;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.utils.ScriptUtil;
import testnet.common.entity.liteflow.ClientToolVersion;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

public class ToolsInstall implements CommonScriptBody {

    private final ILiteFlowMessageSendService sendService;


    public ToolsInstall() {
        this.sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
    }

    @Override
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        sendService.setTaskId(taskExecuteMessage.getTaskId());
        JSONObject params = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        if (params == null) {
            sendService.ERROR("工具安装失败！参数为空");
            return null;
        }
        String installCommand = params.getString("installCommand");
        sendService.INFO("工具安装命令是：" + installCommand);
        String checkVersionCommand = params.getString("versionCheckCommand");
        try {
            ClientToolVersion clientToolVersion = new ClientToolVersion();
            executeCommand(installCommand);
            String version = checkToolVersion(checkVersionCommand);
            clientToolVersion.setToolVersion(version);
            sendService.sendResult(clientToolVersion);
        } catch (Exception e) {
            sendService.ERROR("工具安装过程中发生错误：" + e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }


    private String checkToolVersion(String checkVersionCommand) {
        CommandUtils.CommandResult versionResult = ScriptUtil.execToFile(checkVersionCommand);
        if (versionResult == null) {
            sendService.ERROR("文件创建失败!");
        } else {
            if (versionResult.getExitCode() == 0) {
                if (StringUtils.isNotBlank(versionResult.getOutput())) {
                    sendService.INFO("工具安装成功，版本是：" + versionResult.getOutput());
                    return versionResult.getOutput();
                } else {
                    sendService.INFO("工具未安装");
                }
            } else {
                sendService.ERROR("工具版本检查失败，状态码是：" + versionResult.getExitCode() + ",错误信息：" + versionResult.getOutput());
            }
        }
        return "";
    }

    private void executeCommand(String command) {
        CommandUtils.CommandResult result = ScriptUtil.execToFile(command);
        if (result == null) {
            sendService.ERROR("安装工具文件创建失败!");
        } else if (result.getExitCode() != 0) {
            sendService.ERROR("工具安装失败，状态码是：" + result.getExitCode() + ",错误信息：" + result.getOutput());
        } else {
            sendService.INFO("工具安装成功,执行结果:" + result.getOutput());
        }
    }
}
