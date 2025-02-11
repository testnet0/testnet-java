package testnet.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import testnet.client.config.EnvConfig;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.ResultBase;
import testnet.common.enums.LiteFlowStatusEnums;
import testnet.grpc.ClientMessageProto.ClientResponse;
import testnet.grpc.ClientMessageProto.LogMessage;
import testnet.grpc.ClientMessageProto.ResultMessage;
import testnet.grpc.ClientMessageProto.TaskStatusMessage;
import testnet.grpc.ClientMessageServiceGrpc;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

@Service
@Slf4j
public class LiteFlowMessageSendServiceImpl implements ILiteFlowMessageSendService {

    private static final ThreadLocal<String> taskIdThreadLocal = new ThreadLocal<>();
    @Resource
    private EnvConfig envConfig;

    @GrpcClient("myService")
    private ClientMessageServiceGrpc.ClientMessageServiceBlockingStub clientMessageService;

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
        LogMessage logMessage = LogMessage.newBuilder()
                .setClientName(envConfig.getClientName())
                .setTaskId(taskIdThreadLocal.get())
                .setMessage(formattedMessage)
                .setLevel(level)
                .build();
        sendLog(logMessage);
    }

    @SneakyThrows
    @Override
    public <T> ClientResponse sendWithRetryAndFallback(String type, T data, Function<T, ClientResponse> sendFunction) {
        int maxRetries = 3;
        int retryCount = 0;
        ClientResponse response = null;

        while (retryCount < maxRetries) {
            try {
                response = sendFunction.apply(data);
                if (response.getSuccess()) {
                    return response;
                } else {
                    log.error("{} fail: {}", type, response.getMessage());
                }
            } catch (Exception e) {
                log.error("{} fail (attempt {} of {}): {}", type, retryCount + 1, maxRetries, e.getMessage());
            }

            retryCount++;
            if (retryCount < maxRetries) {
                try {
                    Thread.sleep(60 * 1000L); // Wait for 1 minute before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry interrupted: {}", ie.getMessage());
                    break;
                }
            }
        }

        // If all retries fail, save to SQLite
        log.error("Failed to {} after {} attempts. Saving to SQLite database.", type, maxRetries);
        saveToSQLite(type, data);
        return response; // Return the last response (even if it's null or failed)
    }

    @SneakyThrows
    private <T> void saveToSQLite(String type, T data) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Connect to SQLite database
            connection = DriverManager.getConnection("jdbc:sqlite:fallback.db");
            String sql = "INSERT INTO fallback_data (type, data) VALUES (?, ?)";

            // Prepare the SQL statement
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, JSONObject.toJSONString(data));

            // Execute the SQL statement
            preparedStatement.executeUpdate();
            log.info("Data saved to SQLite database: type={}, data={}", type, data);
        } catch (SQLException e) {
            log.error("Failed to save data to SQLite database: {}", e.getMessage());
        } finally {
            // Close resources
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public ClientResponse sendLog(LogMessage logMessage) {
        return sendWithRetryAndFallback("reportLog", logMessage, clientMessageService::reportLog);
    }

    public ClientResponse sendStatus(String status) {
        TaskStatusMessage clientStatus = TaskStatusMessage.newBuilder()
                .setTaskId(taskIdThreadLocal.get())
                .setTaskStatus(status)
                .build();
        return sendWithRetryAndFallback("reportTaskStatus", clientStatus, clientMessageService::reportTaskStatus);
    }

    @Override
    public <T extends ResultBase> ClientResponse sendResult(T result) {
        ResultMessage liteFlowResult = ResultMessage.newBuilder()
                .setTaskId(taskIdThreadLocal.get())
                .setResult(JSONObject.toJSONString(result))
                .build();
        return sendWithRetryAndFallback("reportResult", liteFlowResult, clientMessageService::reportResult);
    }

    public <T extends ResultBase> ClientResponse sendResult(ResultMessage result) {
        return sendWithRetryAndFallback("reportResult", result, clientMessageService::reportResult);
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

    private static String formatMessage(String message, Object... args) {
        if (message == null) {
            log.error("Message is null.");
            return null;
        }

        // 统计占位符数量
        long placeholderCount = message.chars().filter(ch -> ch == '{').count();
        if (placeholderCount != args.length) {
            log.error("The number of placeholders does not match the number of arguments.");
            return message;
        }

        StringBuilder sb = new StringBuilder(message);
        int placeholderIndex = 0;
        int offset = 0; // 用于记录替换后的偏移量

        while (placeholderIndex < args.length) {
            int startIndex = sb.indexOf("{}", offset);
            if (startIndex == -1) break; // 所有占位符已替换，退出循环

            // 替换占位符
            String replacement = args[placeholderIndex] != null ? args[placeholderIndex].toString() : "null";
            sb.replace(startIndex, startIndex + 2, replacement);

            // 更新偏移量
            offset = startIndex + replacement.length();
            placeholderIndex++;
        }

        return sb.toString();
    }
}