package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.asset.AssetLabel;
import org.jeecg.modules.testnet.server.mapper.asset.AssetLabelMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetLabelService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * @Description: 资产标签
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetLabelServiceImpl extends ServiceImpl<AssetLabelMapper, AssetLabel> implements IAssetLabelService {


    @Override
    @Cacheable(value = "asset:label:cache", key = "#labelName", unless = "#result == null ")
    public AssetLabel getAssetLabelByAssetName(String labelName) {
        QueryWrapper<AssetLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("label_name", labelName);
        return getOne(queryWrapper);
    }

    @Override
    @CacheEvict(value = "asset:label:cache", key = "#assetLabel.labelName")
    public void update(AssetLabel assetLabel) {
        updateById(assetLabel);
    }

    @Override
    @CacheEvict(value = "asset:label:cache", key = "#assetLabel.labelName")
    public void delete(AssetLabel assetLabel) {
        removeById(assetLabel.getId());
    }


}
