package org.jeecg.modules.testnet.server.service.asset.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.asset.AssetBlackList;
import org.jeecg.modules.testnet.server.mapper.asset.AssetBlackListMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetBlackListService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 黑名单
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetBlackListServiceImpl extends ServiceImpl<AssetBlackListMapper, AssetBlackList> implements IAssetBlackListService {


    @Override
    @Cacheable(value = "asset:blacklistAsset:cache", key = "#assetType", unless = "#result == null ")
    public List<AssetBlackList> getBlackList(String assetType) {
        LambdaQueryWrapper<AssetBlackList> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AssetBlackList::getAssetType, assetType);
        return list(lambdaQueryWrapper);
    }

    @Override
    @CacheEvict(value = "asset:blacklistAsset:cache", allEntries = true)
    public void clearCache() {

    }


}
