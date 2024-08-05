package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSubDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetVulMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.asset.AssetSubDomainVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @Description: 子域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetSubDomainServiceImpl extends ServiceImpl<AssetSubDomainMapper, AssetSubDomain> implements IAssetService<AssetSubDomain, AssetSubDomain, AssetSubDomainIpsDTO> {

    @Resource
    private IAssetIpSubdomainRelationService assetIpSubdomainRelationService;
    @Resource
    private IAssetValidService assetValidService;

    @Resource
    private AssetWebServiceImpl assetWebService;

    @Resource
    private AssetVulMapper assetVulMapper;

    @Override
    public IPage<AssetSubDomain> page(IPage<AssetSubDomain> page, QueryWrapper<AssetSubDomain> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("ip_id")) {
            String ipId = parameterMap.get("ip_id")[0];
            queryWrapper.in("id", assetIpSubdomainRelationService.getSubDomainIdsByIpId(ipId));
        }
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetSubDomainVO convertVO(AssetSubDomain record) {
        AssetSubDomainVO assetSubDomainVO = new AssetSubDomainVO();
        BeanUtils.copyProperties(record, assetSubDomainVO);
        assetSubDomainVO.setIpList(assetIpSubdomainRelationService.getAssetIpBySubDomainId(record.getId()));
        assetSubDomainVO.setAssetWebVOList(assetWebService.getWebBySubDomainId(record.getId()));
        return assetSubDomainVO;
    }

    @Override
    public AssetSubDomainIpsDTO convertDTO(AssetSubDomain asset) {
        AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
        BeanUtils.copyProperties(asset, assetSubDomainIpsDTO);
        assetSubDomainIpsDTO.setIps(assetIpSubdomainRelationService.getIpsBySubDomainId(asset.getId()));
        return assetSubDomainIpsDTO;
    }

    @Override
    public boolean addAssetByType(AssetSubDomainIpsDTO asset) {
        save(asset);
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return true;
    }

    @Override
    public boolean updateAssetByType(AssetSubDomainIpsDTO asset) {
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            assetIpSubdomainRelationService.delByAssetSubDomainId(id);
            List<String> assetWebList = assetWebService.getWebIdBySubDomainId(id);
            assetWebService.delRelation(assetWebList);
            assetVulMapper.delBySubDomainId(id);
            removeById(id);
        });
    }

    @Override
    public boolean saveBatch(Collection<AssetSubDomain> entityList) {
        List<AssetSubDomain> assetSubDomainList = new ArrayList<>();
        for (AssetSubDomain assetSubDomain : entityList) {
            if (assetValidService.isValid(assetSubDomain, AssetTypeEnums.SUB_DOMAIN)) {
                if (assetValidService.getUniqueAsset(assetSubDomain, this, AssetTypeEnums.SUB_DOMAIN) == null) {
                    assetSubDomainList.add(assetSubDomain);
                } else {
                    log.info("子域名:{} 重复，跳过", assetSubDomain);
                }
            }
        }
        return super.saveBatch(assetSubDomainList);

    }

    @Override
    public List<AssetSubDomain> list(Wrapper<AssetSubDomain> queryWrapper) {
        List<AssetSubDomain> assetSubDomainList = this.getBaseMapper().selectList(queryWrapper);
        List<AssetSubDomain> newAssetSubDomainList = new ArrayList<>();
        for (AssetSubDomain assetSubDomain : assetSubDomainList) {
            String ip = assetIpSubdomainRelationService.getIpsBySubDomainId(assetSubDomain.getId());
            if (ip != null) {
                assetSubDomain.setIp(ip);
                newAssetSubDomainList.add(assetSubDomain);
            }
        }
        return newAssetSubDomainList;
    }
}
