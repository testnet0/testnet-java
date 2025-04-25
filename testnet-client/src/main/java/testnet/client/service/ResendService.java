package testnet.client.service;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import testnet.client.config.DatabaseConfig;
import testnet.client.service.impl.LiteFlowMessageSendServiceImpl;
import testnet.grpc.ClientMessageProto.ClientResponse;
import testnet.grpc.ClientMessageProto.LogMessage;
import testnet.grpc.ClientMessageProto.ResultMessage;
import testnet.grpc.ClientMessageProto.TaskStatusMessage;

import javax.annotation.Resource;
import java.sql.*;

@Service
@Slf4j
public class ResendService implements ApplicationRunner {

    @Resource
    private LiteFlowMessageSendServiceImpl clientMessageService;

    @Resource
    private DatabaseConfig databaseConfig;

    @Override
    public void run(ApplicationArguments args) {
        createFallbackTable();
        resendFailedMessages();
    }

    @SneakyThrows
    public void resendFailedMessages() {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        PreparedStatement deleteStatement = null;
        ResultSet resultSet = null;

        try {
            // Connect to SQLite database
            String url = databaseConfig.getDatabaseUrl();
            connection = DriverManager.getConnection(url);
            String selectSql = "SELECT id, type, data FROM fallback_data";
            String deleteSql = "DELETE FROM fallback_data WHERE id = ?";

            // Prepare the SQL statements
            selectStatement = connection.prepareStatement(selectSql);
            deleteStatement = connection.prepareStatement(deleteSql);

            // Execute the SELECT query
            resultSet = selectStatement.executeQuery();

            // Process each row
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String type = resultSet.getString("type");
                String data = resultSet.getString("data");

                // Attempt to resend the data
                boolean resendSuccess = resendData(type, data);

                // If resend is successful, delete the record from the database
                if (resendSuccess) {
                    deleteStatement.setLong(1, id);
                    deleteStatement.executeUpdate();
                    log.info("Resend successful for record id={}. Deleted from fallback database.", id);
                } else {
                    log.error("Resend failed for record id={}. Keeping in fallback database.", id);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to resend messages from SQLite database: {}", e.getMessage());
        } finally {
            // Close resources
            if (resultSet != null) resultSet.close();
            if (selectStatement != null) selectStatement.close();
            if (deleteStatement != null) deleteStatement.close();
            if (connection != null) connection.close();
        }
    }

    private boolean resendData(String type, String data) {
        try {
            switch (type) {
                case "reportLog":
                    LogMessage logMessage = JSONObject.parseObject(data, LogMessage.class);
                    ClientResponse logResponse = clientMessageService.sendLog(logMessage);
                    return logResponse.getSuccess();
                case "reportTaskStatus":
                    TaskStatusMessage statusMessage = JSONObject.parseObject(data, TaskStatusMessage.class);
                    ClientResponse statusResponse = clientMessageService.sendStatus(statusMessage.getTaskStatus());
                    return statusResponse.getSuccess();
                case "reportResult":
                    ResultMessage resultMessage = JSONObject.parseObject(data, ResultMessage.class);
                    ClientResponse resultResponse = clientMessageService.sendResult(resultMessage);
                    return resultResponse.getSuccess();
                default:
                    log.error("Unknown message type: {}", type);
                    return false;
            }
        } catch (Exception e) {
            log.error("Failed to resend data of type {}: {}", type, e.getMessage());
            return false;
        }
    }

    @SneakyThrows
    public void createFallbackTable() {
        Connection connection = null;
        Statement statement = null;

        try {
            // 手动加载 SQLite 驱动
            Class.forName("org.sqlite.JDBC");
            // 使用配置文件中的数据库路径
            String url = databaseConfig.getDatabaseUrl();
            connection = DriverManager.getConnection(url);

            // 创建 fallback_data 表（如果不存在）
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS fallback_data (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT NOT NULL, " +
                    "data TEXT NOT NULL)";
            statement.execute(sql);
            log.info("Table 'fallback_data' created or already exists.");
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Failed to create fallback_data table: {}", e.getMessage());
        } finally {
            // 关闭资源
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}