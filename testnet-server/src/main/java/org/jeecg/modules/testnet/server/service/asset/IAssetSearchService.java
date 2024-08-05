package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.dto.AssetSearchImportDTO;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.vo.AssetSearchVO;

public interface IAssetSearchService {

    Result<IPage<AssetSearchVO>> list(AssetSearchDTO assetSearchDTO);

    void importAsset(AssetSearchImportDTO assetSearchImportDTO);

    void executeAgain(LiteFlowTask liteFlowTask);
}
