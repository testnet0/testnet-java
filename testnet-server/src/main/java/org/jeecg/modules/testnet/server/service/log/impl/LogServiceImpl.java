package org.jeecg.modules.testnet.server.service.log.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.service.log.ILogService;
import org.jeecg.modules.testnet.server.service.lucene.LuceneService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class LogServiceImpl implements ILogService {

    @Resource
    private LuceneService luceneService;


    @Override
    @Async
    public void addINFOLog(String from, String log, String subTaskId) {
        if (StringUtils.isNotBlank(subTaskId)) {
            luceneService.addLogDocument(subTaskId, from, log, "INFO");
        }
    }

    @Override
    @Async
    public void addERRORLog(String from, String log, String subTaskId) {
        if (StringUtils.isNotBlank(subTaskId)) {
            luceneService.addLogDocument(subTaskId, from, log, "ERROR");
        }
    }

}
