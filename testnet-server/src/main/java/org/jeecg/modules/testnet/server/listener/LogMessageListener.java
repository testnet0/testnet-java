package org.jeecg.modules.testnet.server.listener;

import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.LogMessage;
import testnet.common.service.IRedisStreamService;
import testnet.common.utils.ObjectBase64Decoder;

import javax.annotation.Resource;

@Component
@Slf4j
public class LogMessageListener implements StreamListener<String, ObjectRecord<String, LogMessage>> {

    @Resource
    private JeecgElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private IRedisStreamService redisStreamService;

    @SneakyThrows
    @Override
    @Async("logMessageExecutor")
    public void onMessage(ObjectRecord<String, LogMessage> record) {
        LogMessage logMessage = ObjectBase64Decoder.decodeFields(record.getValue());
        redisStreamService.ack(record.getStream(), Constants.STREAM_KEY_TASK_EXECUTE, record.getId().getValue());
        try {
            log.info("接收到日志消息: {}", logMessage);
            String taskId = logMessage.getTaskId();
            String level = logMessage.getLevel();
            String message = logMessage.getMessage();
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(logMessage);
            jsonObject.put("message", Base64Encoder.encode(message));
            elasticsearchTemplate.save(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, record.getId().toString(), jsonObject);
            if (taskId.isEmpty() || level.isEmpty()) {
                log.error("关键参数为空！pluginInstanceId: {}, level: {}", taskId, level);
                return;
            }
            switch (level) {
                case "INFO":
                    log.info("实例ID：{}， 运行日志：{}", taskId, message);
                    break;
                case "ERROR":
                    log.error("实例ID：{}，错误日志：{}", taskId, message);
                    break;
            }
            redisStreamService.del(record.getStream(), record.getId().getValue());
        } catch (Exception e) {
            int count = Integer.parseInt(logMessage.getRetryCount());
            if (count <= 5) {
                logMessage.setRetryCount(String.valueOf(count + 1));
                log.error("处理日志出现异常，重试第{}次", logMessage.getRetryCount());
                redisStreamService.addObject(record.getStream(), logMessage);
            } else {
                log.error("处理日志出现异常，重试次数过多，放弃重试");
            }
        }
    }


}