package org.jeecg.modules.testnet.server.service.search;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.vo.asset.AssetSearchVO;

public interface ISearchEngineService {
    Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, AssetSearchEngine assetSearchEngine);
}
