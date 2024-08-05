package testnet.client.listener;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.flow.entity.CmpStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import testnet.client.config.EnvConfig;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.service.IRedisStreamService;
import testnet.common.utils.ObjectBase64Decoder;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class TaskExecuteMessageListener implements StreamListener<String, ObjectRecord<String, TaskExecuteMessage>> {


    @Resource
    private ILiteFlowMessageSendService liteFlowMessageSendService;

    @Resource
    private IRedisStreamService redisStreamService;

    @Resource
    private FlowExecutor flowExecutor;

    @Resource
    private EnvConfig envConfig;


    @Override
    @Async
    public void onMessage(ObjectRecord<String, TaskExecuteMessage> record) {
        TaskExecuteMessage taskExecuteMessage;
        try {
            taskExecuteMessage = ObjectBase64Decoder.decodeFields(record.getValue());
        } catch (Exception e) {
            log.error("执行任务解码失败，错误信息:{}", e.getMessage());
            return;
        }
        log.info("Chanel:{} receive message,messageId:{}, content:{}", record.getStream(), record.getId(), taskExecuteMessage);
        liteFlowMessageSendService.setTaskId(taskExecuteMessage.getTaskId());
        redisStreamService.ack(record.getStream(), Constants.STREAM_KEY_TASK_EXECUTE, record.getId().getValue());
        redisStreamService.del(record.getStream(), record.getId().getValue());
        liteFlowMessageSendService.INFO("任务:{}开始执行", taskExecuteMessage.getChainName());
        // 设置结果保持路径
        taskExecuteMessage.setResultPath(envConfig.getResultPath());
        LiteflowResponse response = flowExecutor.execute2Resp(taskExecuteMessage.getChainName(), taskExecuteMessage, LiteFlowResult.class);
        if (response.isSuccess()) {
            liteFlowMessageSendService.SUCCESS();
            Map<String, List<CmpStep>> stepMap = response.getExecuteSteps();
            stepMap.forEach((s, cmpSteps) -> cmpSteps.forEach(cmpStep -> liteFlowMessageSendService.INFO("任务:{}执行步骤{}耗时:{}ms", taskExecuteMessage.getChainName(), cmpStep.getNodeName(), cmpStep.getTimeSpent())));
        } else {
            liteFlowMessageSendService.FAILED();
            liteFlowMessageSendService.ERROR("任务 {} 执行失败！参数:{}, 原因:{}", taskExecuteMessage.getChainName(), taskExecuteMessage.getConfig(), response.getCause());
        }
    }
}
