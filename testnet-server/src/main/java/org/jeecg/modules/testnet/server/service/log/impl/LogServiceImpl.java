package org.jeecg.modules.testnet.server.service.log.impl;

import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.jeecg.modules.testnet.server.service.log.ILogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.LogMessage;

import javax.annotation.Resource;
import java.util.UUID;

@Service
@Slf4j
public class LogServiceImpl implements ILogService {

    @Resource
    private JeecgElasticsearchTemplate elasticsearchTemplate;

    @Override
    @Async
    public void addINFOLog(String from, String log, String subTaskId) {
        if (StringUtils.isNotBlank(subTaskId)) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTaskId(subTaskId);
            logMessage.setClientName(from);
            logMessage.setMessage(Base64Encoder.encode(log));
            logMessage.setLevel("INFO");
            elasticsearchTemplate.save(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, subTaskId + UUID.randomUUID(), (JSONObject) JSONObject.toJSON(logMessage));
        }
    }

    @Override
    @Async
    public void addERRORLog(String from, String log, String subTaskId) {
        if (StringUtils.isNotBlank(subTaskId)) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTaskId(subTaskId);
            logMessage.setClientName(from);
            logMessage.setMessage(Base64Encoder.encode(log));
            logMessage.setLevel("ERROR");
            elasticsearchTemplate.save(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, subTaskId + UUID.randomUUID(), (JSONObject) JSONObject.toJSON(logMessage));
        }
    }
}
