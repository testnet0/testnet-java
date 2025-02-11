/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-16
 **/
package org.jeecg.modules.testnet.server.schedule;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class CancelTask {

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;

    @Scheduled(fixedRate = 60 * 60 * 1000L) // 每小时刷新一次
    public void cancelTask() {
        liteFlowSubTaskService.getRunningList().forEach(subTask -> {
            Instant now = Instant.now();
            Instant updateTime = subTask.getUpdateTime().toInstant();
            // 计算两个时间点之间的持续时间
            Duration duration = Duration.between(updateTime, now);
            // 如果更新时间超过两小时，则取消子任务
            if (duration.toHours() > 2) {
                liteFlowSubTaskService.cancelSubTask(subTask.getId());
            }
        });
    }
}
