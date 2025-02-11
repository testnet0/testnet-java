package testnet.client.service;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.flow.entity.CmpStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import testnet.client.config.EnvConfig;
import testnet.common.entity.liteflow.TaskExecuteMessage;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RunTaskService {

    @Resource
    private ILiteFlowMessageSendService liteFlowMessageSendService;

    @Resource
    private FlowExecutor flowExecutor;

    @Resource
    private EnvConfig envConfig;


    @Async
    public void executeTask(TaskExecuteMessage taskExecuteMessage) {
        liteFlowMessageSendService.setTaskId(taskExecuteMessage.getTaskId());
        // 设置结果保存路径
        taskExecuteMessage.setResultPath(envConfig.getResultPath());
        try {
            LiteflowResponse liteflowResponse = flowExecutor.execute2Resp(taskExecuteMessage.getChainName(), taskExecuteMessage);
            if (liteflowResponse.isSuccess()) {
                liteFlowMessageSendService.SUCCESS();
                Map<String, List<CmpStep>> stepMap = liteflowResponse.getExecuteSteps();
                stepMap.forEach((s, cmpSteps) -> cmpSteps.forEach(cmpStep -> liteFlowMessageSendService.INFO("任务:{}执行步骤{}耗时:{}ms", taskExecuteMessage.getChainName(), cmpStep.getNodeName(), cmpStep.getTimeSpent())));
            } else {
                liteFlowMessageSendService.FAILED();
                liteFlowMessageSendService.ERROR("任务 {} 执行失败！参数:{}, 原因:{}", taskExecuteMessage.getChainName(), taskExecuteMessage.getConfig(), liteflowResponse.getCause());
            }
        } catch (Exception e) {
            liteFlowMessageSendService.ERROR("任务 {} 执行失败！参数:{}, 原因:{}", taskExecuteMessage.getChainName(), taskExecuteMessage.getConfig(), e.getMessage());
        }
    }
}
