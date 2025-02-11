/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-03
 **/
package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSearchEngineMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchEngineService;
import org.springframework.stereotype.Service;

@Service
public class AssetSearchEngineServiceImpl extends ServiceImpl<AssetSearchEngineMapper, AssetSearchEngine> implements IAssetSearchEngineService {

    @Override
    public AssetSearchEngine getKey(String engine) {
        LambdaQueryWrapper<AssetSearchEngine> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AssetSearchEngine::getEngineName, engine);
        return getOne(lambdaQueryWrapper);
    }
}
