package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.asset.AssetDomainDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.mapper.asset.AssetDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSubDomainMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.asset.AssetDomainVO;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description: 主域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetDomainServiceImpl extends ServiceImpl<AssetDomainMapper, AssetDomain> implements IAssetService<AssetDomain, AssetDomainVO, AssetDomain> {

    @Resource
    private AssetSubDomainMapper assetSubDomainMapper;

    @Resource
    private IAssetValidService assetValidService;

    @Resource
    private AssetSubDomainServiceImpl assetSubDomainService;

    @Override
    public IPage<AssetDomain> page(IPage<AssetDomain> page, QueryWrapper<AssetDomain> queryWrapper, Map<String, String[]> parameterMap) {
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetDomainVO convertVO(AssetDomain record) {
        AssetDomainVO assetDomainVO = new AssetDomainVO();
        BeanUtil.copyProperties(record, assetDomainVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        assetDomainVO.setSubDomainNumber(assetSubDomainMapper.getSubDomainCountByDomain(record.getId()));
        return assetDomainVO;
    }

    @Override
    public AssetDomainDTO convertDTO(AssetDomain asset) {
        AssetDomainDTO assetDomainDTO = new AssetDomainDTO();
        BeanUtil.copyProperties(asset, assetDomainDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        return assetDomainDTO;
    }

    @Override
    public boolean addAssetByType(AssetDomain asset) {
        return save(asset);
    }

    @Override
    public boolean updateAssetByType(AssetDomain asset) {
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            List<String> subDomains = assetSubDomainMapper.getSubDomainIdsListByDomain(id);
            assetSubDomainService.delRelation(subDomains);
            removeById(id);
        });
    }


    public boolean saveBatch(Collection<AssetDomain> entityList) {
        List<AssetDomain> assetDomainList = new ArrayList<>();
        for (AssetDomain assetDomain : entityList) {
            if (assetValidService.isValid(assetDomain, AssetTypeEnums.DOMAIN)) {
                if (assetValidService.getUniqueAsset(assetDomain, this, AssetTypeEnums.DOMAIN) == null) {
                    assetDomainList.add(assetDomain);
                } else {
                    log.info("主域名:{} 重复,跳过", assetDomain);
                }
            }
        }
        return super.saveBatch(assetDomainList);
    }
}
