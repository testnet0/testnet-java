package org.jeecg.modules.testnet.server.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.jeecg.modules.message.handle.impl.WebhookMsgHandle;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.service.IRedisStreamService;
import testnet.common.utils.ObjectBase64Decoder;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class ResultMessageListener implements StreamListener<String, ObjectRecord<String, LiteFlowResult>> {


    @Resource
    private IRedisStreamService redisStreamService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;

    @Resource
    private IChainService chainService;

    @Resource
    private Map<String, IAssetResultProcessorService> assetResultProcessMap;

    @Resource
    private ILiteFlowTaskService liteFlowTaskService;

    @Resource
    private WebhookMsgHandle webhookMsgHandle;


    @SneakyThrows
    @Override
    @Async("resultMessageExecutor")
    public void onMessage(ObjectRecord<String, LiteFlowResult> record) {
        LiteFlowResult liteFLowResult = ObjectBase64Decoder.decodeFields(record.getValue());
        redisStreamService.ack(record.getStream(), Constants.STREAM_KEY_RESULT, record.getId().getValue());
        redisStreamService.del(record.getStream(), record.getId().getValue());
        log.info("接收到结果消息: {}", liteFLowResult);
        String taskId = liteFLowResult.getTaskId();
        LiteFlowSubTask liteFlowSubTask = liteFlowSubTaskService.getById(taskId);
        if (liteFlowSubTask == null) {
            log.error("实例不存在: {}", taskId);
        } else {
            LiteFlowTask liteFlowTask = liteFlowTaskService.getByIdWithCache(liteFlowSubTask.getTaskId());
            Chain chain = chainService.getByIdWithCache(liteFlowTask.getChainId());
            if (chain != null && StringUtils.isNotBlank(chain.getProcessorClassName())) {
                IAssetResultProcessorService assetResultProcessService = assetResultProcessMap.get(chain.getProcessorClassName());
                if (assetResultProcessService != null) {
                    log.info("开始处理结果,类名:{}", chain.getProcessorClassName());
                    String assetId = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam()).getString("id");
                    assetResultProcessService.processAsset(assetId, chain.getChainName(), liteFlowTask, liteFlowSubTask, liteFLowResult);
                } else {
                    log.error("未找到处理类: {}", chain.getProcessorClassName());
                }
            }
        }

    }
}