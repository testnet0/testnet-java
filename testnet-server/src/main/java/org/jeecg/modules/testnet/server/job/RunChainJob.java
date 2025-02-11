/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.quartz.*;

import javax.annotation.Resource;


@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class RunChainJob implements Job {

    @Resource
    private ILiteFlowTaskService liteFlowTaskService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 获取JobDetail
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        // 获取JobDataMap
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String id = jobDataMap.getString("parameter");
        if (StringUtils.isNotBlank(id)) {
            log.info("开始执行任务，参数:{}", id);
            liteFlowTaskService.executeAgain(id, false);
        }
    }

}
