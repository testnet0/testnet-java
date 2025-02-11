package org.jeecg.modules.testnet.server.service.search;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.vo.AssetSearchVO;

public interface ISearchEngineService {
    Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, String key);
}
