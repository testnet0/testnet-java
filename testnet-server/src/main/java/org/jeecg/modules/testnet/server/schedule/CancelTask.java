/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-16
 **/
package org.jeecg.modules.testnet.server.schedule;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.boot.starter.lock.client.RedissonLockClient;
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

    @Resource
    private RedissonLockClient redissonLockClient;

    @Scheduled(fixedRate = 60 * 1000L, initialDelay = 60 * 1000L) // 每分钟刷新一次
    public void cancelTask() {
        liteFlowSubTaskService.getRunningList().forEach(subTask -> {
            Instant now = Instant.now();
            Instant updateTime = subTask.getUpdateTime().toInstant();
            // 计算两个时间点之间的持续时间
            Duration duration = Duration.between(updateTime, now);
            JSONObject jsonObject = JSONObject.parseObject(subTask.getConfig());
            if (jsonObject != null && jsonObject.containsKey("timeout")) {
                // 如果更新时间超过timeout分钟，则取消子任务
                if (duration.toMinutes() > jsonObject.getLong("timeout")) {
                    if (redissonLockClient.tryLock(subTask.getId(), 10, 10)) {
                        liteFlowSubTaskService.cancelSubTask(subTask.getId());
                    }
                }
            } else {
                // 如果更新时间超过两小时，则取消子任务
                if (duration.toHours() > 2) {
                    if (redissonLockClient.tryLock(subTask.getId(), 10, 10)) {
                        liteFlowSubTaskService.cancelSubTask(subTask.getId());
                    }
                }
            }
        });
    }
}
