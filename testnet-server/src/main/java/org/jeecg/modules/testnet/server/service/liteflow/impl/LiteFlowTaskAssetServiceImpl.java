package org.jeecg.modules.testnet.server.service.liteflow.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTaskAsset;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskAssetMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskAssetService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

/**
 * @Description: 任务关联资产表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class LiteFlowTaskAssetServiceImpl extends ServiceImpl<LiteFlowTaskAssetMapper, LiteFlowTaskAsset> implements ILiteFlowTaskAssetService {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Override
    @Async
    public void deleteByTask(String id) {
        LambdaQueryWrapper<LiteFlowTaskAsset> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiteFlowTaskAsset::getLiteFlowTaskId, id);
        list(queryWrapper).forEach(item -> {
            assetCommonOptionService.delByIdAndAssetType(item.getAssetId(), AssetTypeEnums.fromCode(item.getAssetType()));
            removeById(item.getId());
        });
    }
}

