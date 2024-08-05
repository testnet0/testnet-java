package org.jeecg.modules.testnet.server.service.liteflow;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTaskAsset;

public interface ILiteFlowTaskAssetService extends IService<LiteFlowTaskAsset> {

    void deleteByTask(String id);
}
