package org.jeecg.modules.testnet.server.service.asset.impl;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.springframework.cache.annotation.CacheEvict;
import testnet.common.enums.AssetTypeEnums;


public interface IAssetCacheService {

    <D extends AssetBase> Result<? extends AssetBase> getAssetDOByIdAndAssetType(String id, AssetTypeEnums assetType);

    void removeCacheByIdAndAssetType(String id, AssetTypeEnums assetType);
}
