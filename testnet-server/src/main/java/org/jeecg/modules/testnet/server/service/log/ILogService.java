package org.jeecg.modules.testnet.server.service.log;

public interface ILogService {
    void addINFOLog(String from, String log, String subTaskId);

    void addERRORLog(String from, String log, String subTaskId);
}
