package testnet.client.service.grpc;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sun.management.OperatingSystemMXBean;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import testnet.client.config.EnvConfig;
import testnet.client.service.RunTaskService;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.grpc.ClientMessageProto.ClientResponse;
import testnet.grpc.ClientMessageProto.ClientStatusMessage;
import testnet.grpc.ClientMessageServiceGrpc;

import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.util.List;

@Service
@Slf4j
public class VersionReportService {
    @Resource
    private EnvConfig envConfig;

    @Resource
    private RunTaskService runTaskService;


    @GrpcClient("myService")
    private ClientMessageServiceGrpc.ClientMessageServiceBlockingStub clientMessageService;

    public void reportVersion() {
        double cpuLoad = 0;
        int totalMemory = 0;
        int freeMemory = 0;
       try {
           OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            cpuLoad = osBean.getSystemCpuLoad();
            totalMemory = (int) (osBean.getTotalPhysicalMemorySize() / (1024 * 1024));
            freeMemory = (int) (osBean.getFreePhysicalMemorySize() / (1024 * 1024));
       } catch (Exception e) {
           log.error("获取CPU使用率失败，错误信息: {}", e.getMessage());
       }
        ClientStatusMessage clientInfo = ClientStatusMessage.newBuilder()
                .setClientName(envConfig.getClientName())
                .setClientVersion(envConfig.getClientVersion())
                .setCpuUsage(cpuLoad * 100)
                .setTotalMemory(totalMemory)
                .setFreeMemory(freeMemory)
                .build();
        try {
            ClientResponse response = clientMessageService.reportClientStatus(clientInfo);
            if (!response.getSuccess()) {
                log.info("Report client status failed: {}", response.getMessage());
            } else {
                List<TaskExecuteMessage> taskExecuteMessages = JSON.parseArray(response.getMessage(), TaskExecuteMessage.class);
                if (!taskExecuteMessages.isEmpty()) {
                    log.info("receive message, content:{}", taskExecuteMessages);
                    taskExecuteMessages.forEach(runTaskService::executeTask);
                }
            }
        } catch (Exception e) {
            if (e instanceof StatusRuntimeException) {
                log.error("连接服务端失败，错误信息:{}", e.getMessage());
            } else {
                log.error("上报客户端状态失败，错误信息: {}", e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 5 * 1000L, initialDelay = 20 * 1000L) // 每5秒执行一次,启动后延迟20秒执行
    public void clientVersion() {
        reportVersion();
    }


}
