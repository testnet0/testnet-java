package org.jeecg.modules.testnet.server.service.asset.impl;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

@Service
public class IAssetCacheServiceImpl implements IAssetCacheService {
    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Override
    @Cacheable(value = "assetCache", key = "#id+':'+#assetType.name()", unless = "#result == null ")
    public <D extends AssetBase> Result<? extends AssetBase> getAssetDOByIdAndAssetType(String id, AssetTypeEnums assetType) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, assetType);
    }

    @CacheEvict(value = "assetCache", key = "#id+':'+#assetType.name()")
    @Override
    public void removeCacheByIdAndAssetType(String id, AssetTypeEnums assetType) {

    }
}
