package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.mapper.asset.AssetDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSubDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetVulMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.vo.asset.AssetSubDomainVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import testnet.common.utils.DomainUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private AssetWebMapper assetWebMapper;

    @Resource
    private AssetDomainMapper assetDomainMapper;

    @Resource
    private AssetVulMapper assetVulMapper;


    @Override
    public IPage<AssetSubDomain> page(IPage<AssetSubDomain> page, QueryWrapper<AssetSubDomain> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("ip")) {
            queryWrapper.inSql("id", "SELECT aisd.subdomain_id FROM asset_ip_sub_domain aisd LEFT JOIN asset_ip ai ON ai.id = aisd.ip_id WHERE ai.ip LIKE '%" + parameterMap.get("ip")[0] + "%'");
        }
        if (parameterMap != null && parameterMap.containsKey("domain")) {
            queryWrapper.inSql("domain_id", "select id from asset_domain where domain like '%" + parameterMap.get("domain")[0] + "%'");
        }
        return super.page(page, queryWrapper);
    }

    @Override
    public List<AssetSubDomain> list(QueryWrapper<AssetSubDomain> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("ip")) {
            queryWrapper.inSql("id", "SELECT aisd.subdomain_id FROM asset_ip_sub_domain aisd LEFT JOIN asset_ip ai ON ai.id = aisd.ip_id WHERE ai.ip LIKE '%" + parameterMap.get("ip")[0] + "%'");
        }
        if (parameterMap != null && parameterMap.containsKey("domain")) {
            queryWrapper.inSql("domain_id", "select id from asset_domain where domain like '%" + parameterMap.get("domain")[0] + "%'");
        }
        return super.list(queryWrapper);
    }

    @Override
    public AssetSubDomainVO convertVO(AssetSubDomain record) {
        AssetSubDomainVO assetSubDomainVO = new AssetSubDomainVO();
        BeanUtils.copyProperties(record, assetSubDomainVO);
        assetSubDomainVO.setIpList(assetIpSubdomainRelationService.getAssetIpBySubDomainId(record.getId()));
        assetSubDomainVO.setAssetWebVOList(assetWebMapper.getWebBySubDomainId(record.getId()));
        AssetDomain assetDomain = assetDomainMapper.selectById(record.getDomainId());
        if (assetDomain != null) {
            assetSubDomainVO.setDomainLabel(assetDomain.getAssetLabel());
        }
        return assetSubDomainVO;
    }

    @Override
    public AssetSubDomainIpsDTO convertDTO(AssetSubDomain asset) {
        AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
        BeanUtils.copyProperties(asset, assetSubDomainIpsDTO);
        assetSubDomainIpsDTO.setIpList(assetIpSubdomainRelationService.getAssetIpBySubDomainId(asset.getId()));
        assetSubDomainIpsDTO.setIps(assetIpSubdomainRelationService.getIpsBySubDomainId(asset.getId()));
        return assetSubDomainIpsDTO;
    }

    @Override
    public boolean addAssetByType(AssetSubDomainIpsDTO asset) {
        String subDomain = asset.getSubDomain();
        String topDomain = DomainUtils.getTopDomain(subDomain);
        if (StringUtils.isNotEmpty(topDomain)) {
            AssetDomain existingDomain = assetDomainMapper.selectOne(new QueryWrapper<AssetDomain>().eq("domain", topDomain).eq("project_id", asset.getProjectId()));
            if (existingDomain == null) {
                existingDomain = new AssetDomain();
                existingDomain.setDomain(topDomain);
                existingDomain.setProjectId(asset.getProjectId());
                existingDomain.setSource(asset.getSource());
                try {
                    assetDomainMapper.insert(existingDomain);
                } catch (Exception e) {
                    log.warn("主键冲突，重新查询资产: {}", existingDomain, e);
                    existingDomain = assetDomainMapper.selectOne(new QueryWrapper<AssetDomain>().eq("domain", topDomain).eq("project_id", asset.getProjectId()));
                }
            }
            asset.setDomainId(existingDomain.getId());
            save(asset);
            assetIpSubdomainRelationService.addDomainRelation(asset);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateAssetByType(AssetSubDomainIpsDTO asset) {
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            AssetSubDomain assetSubDomain = getById(id);
            if (assetSubDomain != null) {
                evictCache(assetSubDomain.getDomainId(), assetSubDomain.getProjectId());
                assetIpSubdomainRelationService.delByAssetSubDomainId(id);
            }
        });
        assetWebMapper.deleteBySubDomainIds(list);
        assetVulMapper.delBySubDomainIds(list);
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

    @CacheEvict(value = "asset:domain:cache", key = "#domain + ':' + #projectId")
    public void evictCache(String domain, String projectId) {
    }

    @Cacheable(value = "asset:domain:cache", key = "#domain + ':' + #projectId", unless = "#result == null")
    public AssetSubDomain selectBySubdomain(String domain, String projectId) {
        return this.getBaseMapper().selectBySubdomain(domain, projectId);
    }
}
