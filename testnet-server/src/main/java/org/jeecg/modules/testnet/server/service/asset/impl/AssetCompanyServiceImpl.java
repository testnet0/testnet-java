package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.mapper.asset.AssetCompanyMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description: 公司
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetCompanyServiceImpl extends ServiceImpl<AssetCompanyMapper, AssetCompany> implements IAssetService<AssetCompany, AssetCompany, AssetCompany> {

    @Resource
    private IAssetValidService assetValidService;

    @Override
    public IPage<AssetCompany> page(IPage<AssetCompany> page, QueryWrapper<AssetCompany> queryWrapper, Map<String, String[]> parameterMap) {
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetCompany convertVO(AssetCompany record) {
        return record;
    }

    @Override
    public AssetCompany convertDTO(AssetCompany record) {
        return record;
    }

    @Override
    public boolean addAssetByType(AssetCompany asset) {
        return save(asset);
    }

    @Override
    public boolean updateAssetByType(AssetCompany asset) {
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(this::removeById);
    }

    @Override
    public boolean saveBatch(Collection<AssetCompany> entityList) {
        List<AssetCompany> assetCompanyList = new ArrayList<>();
        for (AssetCompany assetCompany : entityList) {
            if (assetValidService.isValid(assetCompany, AssetTypeEnums.COMPANY)) {
                if (assetValidService.getUniqueAsset(assetCompany, this, AssetTypeEnums.COMPANY) == null) {
                    assetCompanyList.add(assetCompany);
                } else {
                    log.info("公司:{} 重复,跳过", assetCompany);
                }
            }
        }
        return super.saveBatch(assetCompanyList);
    }
}
