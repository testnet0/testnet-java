/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-01-01
 **/
package testnet.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import testnet.client.config.EnvConfig;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.constan.Constants;
import testnet.common.dto.ResultBase;
import testnet.common.entity.liteflow.ClientStatus;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.entity.liteflow.LogMessage;
import testnet.common.entity.liteflow.VersionMessage;
import testnet.common.enums.LiteFlowStatusEnums;
import testnet.common.service.IRedisStreamService;

import javax.annotation.Resource;

@Service
@Slf4j
public class LiteFlowMessageSendServiceImpl implements ILiteFlowMessageSendService {

    private static final ThreadLocal<String> taskIdThreadLocal = new ThreadLocal<>();
    @Resource
    private EnvConfig envConfig;
    @Resource
    private IRedisStreamService redisStreamService;

    private static String formatMessage(String message, Object... args) {
        StringBuilder sb = new StringBuilder(message);
        int placeholderIndex = 0;

        // 确保占位符数量与参数数量相匹配
        long placeholderCount = message.chars().filter(ch -> ch == '{').count();
        if (placeholderCount != (long) args.length) {
            log.error("The number of placeholders does not match the number of arguments.");
            return message;
        }

        while (placeholderIndex < placeholderCount) {
            int startIndex = sb.indexOf("{}", placeholderIndex);
            if (startIndex == -1) break;  // 所有占位符已替换，退出循环

            String replacement = args[placeholderIndex].toString();
            sb.replace(startIndex, startIndex + 2, replacement);

            placeholderIndex++;
        }

        return sb.toString();
    }


    @Override
    public void setTaskId(String taskId) {
        taskIdThreadLocal.set(taskId);
    }

    @Override
    public void INFO(String message, Object... args) {
        sendLog("INFO", message, args);
    }

    @Override
    public void WARN(String message, Object... args) {
        sendLog("WARNING", message, args);
    }

    @Override
    public void ERROR(String message, Object... args) {
        sendLog("ERROR", message, args);
    }

    @SneakyThrows
    public void sendLog(String level, String message, Object... args) {
        String formattedMessage = formatMessage(message, args);
        sendLog(level, formattedMessage);
    }

    @SneakyThrows
    public void sendLog(String level, String message) {
        LogMessage logMessage = new LogMessage();
        logMessage.setTaskId(taskIdThreadLocal.get());
        logMessage.setMessage(message);
        logMessage.setLevel(level);
        logMessage.setClientName(envConfig.getClientName());
        logMessage.setClientVersion(envConfig.getClientVersion());
        String recordId = redisStreamService.addObject(Constants.STREAM_KEY_LOG, logMessage);
        if (recordId == null) {
            log.info("Error sending event: {}", logMessage);
            return;
        }
    }

    @SneakyThrows
    public void sendStatus(String status) {
        ClientStatus clientStatus = new ClientStatus();
        clientStatus.setStatus(status);
        clientStatus.setTaskId(taskIdThreadLocal.get());
        clientStatus.setClientName(envConfig.getClientName());
        clientStatus.setClientVersion(envConfig.getClientVersion());
        String recordId = redisStreamService.addObject(Constants.STREAM_KEY_STATUS, clientStatus);
        if (recordId == null) {
            log.info("Error sending status: {}", clientStatus);
            return;
        }
        log.info("Status send to server success！MessageId:{}", recordId);
    }

    @Override
    public <T extends ResultBase> void sendResult(T result) {
        LiteFlowResult liteFlowResult = new LiteFlowResult();
        liteFlowResult.setTaskId(taskIdThreadLocal.get());
        liteFlowResult.setClientName(envConfig.getClientName());
        liteFlowResult.setResult(JSONObject.toJSONString(result));
        liteFlowResult.setClientVersion(envConfig.getClientVersion());
        String recordId = redisStreamService.addObject(Constants.STREAM_KEY_RESULT, liteFlowResult);
        if (recordId == null) {
            log.info("Error sending result: {}", result);
            return;
        }
        log.info("Result send to server success！MessageId:{}", recordId);
    }

    @Override
    @SneakyThrows
    public void sendVersion() {
        VersionMessage versionMessage = new VersionMessage();
        versionMessage.setClientName(envConfig.getClientName());
        versionMessage.setClientVersion(envConfig.getClientVersion());
        String recordId = redisStreamService.addObject(Constants.STREAM_KEY_VERSION, versionMessage);
        if (recordId == null) {
            log.info("Error sending version: {}", versionMessage);
            return;
        }
        log.trace("Version send to server success！MessageId:{}", recordId);
    }

    @Override
    public void RUNNING() {
        sendStatus(LiteFlowStatusEnums.RUNNING.name());
    }

    @Override
    public void FAILED() {
        sendStatus(LiteFlowStatusEnums.FAILED.name());
    }

    @Override
    public void SUCCESS() {
        sendStatus(LiteFlowStatusEnums.SUCCEED.name());
    }

}
