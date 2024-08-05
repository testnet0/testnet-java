package org.jeecg.modules.testnet.server.service.processer;

import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import testnet.common.entity.liteflow.LiteFlowResult;

public interface IAssetResultProcessorService {
    void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase);
}
