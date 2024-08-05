package org.jeecg.modules.testnet.server.listener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.ClientStatus;
import testnet.common.enums.LiteFlowStatusEnums;
import testnet.common.service.IRedisStreamService;
import testnet.common.utils.ObjectBase64Decoder;

import javax.annotation.Resource;

@Component
@Slf4j
public class StatusMessageListener implements StreamListener<String, ObjectRecord<String, ClientStatus>> {


    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;


    @Resource
    private IRedisStreamService redisStreamService;


    @SneakyThrows
    @Override
    public void onMessage(ObjectRecord<String, ClientStatus> record) {
        ClientStatus clientStatus = ObjectBase64Decoder.decodeFields(record.getValue());
        redisStreamService.ack(record.getStream(), Constants.STREAM_KEY_TASK_EXECUTE, record.getId().getValue());
        log.info("接收到任务ID:{},状态消息: {}", clientStatus.getTaskId(), clientStatus.getStatus());
        String taskId = clientStatus.getTaskId();
        if (taskId.isEmpty()) {
            log.error("插件ID为空！");
            return;
        }
        try {
            LiteFlowSubTask liteFlowSubTask = liteFlowSubTaskService.getById(taskId);
            if (liteFlowSubTask != null) {
                if (liteFlowSubTask.getTaskStatus().equals(LiteFlowStatusEnums.RUNNING.name())) {
                    liteFlowSubTask.setTaskStatus(clientStatus.getStatus());
                    liteFlowSubTaskService.updateById(liteFlowSubTask);
                } else {
                    log.info("任务已经完成，不能修改状态！");
                }
            }
            redisStreamService.del(record.getStream(), record.getId().getValue());
        } catch (Exception e) {
            log.error("更新任务状态失败！,错误原因:{}", e.getMessage());
            int count = Integer.parseInt(clientStatus.getRetryCount());
            if (count <= 5) {
                clientStatus.setRetryCount(String.valueOf(count + 1));
                log.error("处理任务状态出现异常，重试第{}次", clientStatus.getRetryCount());
                redisStreamService.addObject(record.getStream(), clientStatus);
            } else {
                log.error("处理任务状态出现异常，重试次数过多，放弃重试");
            }
        }
    }
}