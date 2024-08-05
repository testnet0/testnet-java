package org.jeecg.modules.testnet.server.service.liteflow.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.mapper.liteflow.ChainMapper;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 流程管理
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class ChainServiceImpl extends ServiceImpl<ChainMapper, Chain> implements IChainService {

    @Resource
    private IClientConfigService clientConfigService;


    @Override
//    @Cacheable(value = "asset:chainList:cache#600", key = "#assetType", unless = "#result == null ")
    public List<Chain> getChainListByAssetType(String assetType) {
        LambdaQueryWrapper<Chain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chain::getEnable, 1);
        queryWrapper.apply("FIND_IN_SET({0}, asset_type) > 0", assetType);
        return list(queryWrapper);
    }

    @Override
    @CacheEvict(value = "asset:chainList:cache#600", allEntries = true)
    public void clearCache() {
    }

    @Override
    public void copyChain(String chainId) {
        Chain chain = getById(chainId);
        if (chain != null) {
            chain.setId(null);
            chain.setChainName(chain.getChainName() + "_copy");
            chain.setCreateTime(new Date());
            chain.setUpdateTime(null);
            save(chain);
            clientConfigService.addConfig(chain);
        }
    }

    @Override
    public void changeStatus(String field, String chainId, Boolean status) {
        Chain chain = getById(chainId);
        if (chain != null) {
            if (field.equals("enable")) {
                chain.setEnable(status);
            }
            updateById(chain);
        }
    }

    @Override
    public List<Chain> getAllChainList() {
        return list();
    }

}