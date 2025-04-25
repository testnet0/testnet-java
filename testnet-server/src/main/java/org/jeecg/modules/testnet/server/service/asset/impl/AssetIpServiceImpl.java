package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;
import org.jeecg.modules.testnet.server.mapper.asset.AssetIpMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetPortMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetVulMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.vo.asset.AssetIpVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description: ip
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetIpServiceImpl extends ServiceImpl<AssetIpMapper, AssetIp> implements IAssetService<AssetIp, AssetIpVO, AssetIpDTO> {

    @Resource
    private IAssetIpSubdomainRelationService assetIpSubdomainRelationService;

    @Resource
    private AssetPortMapper assetPortMapper;


    @Resource
    private AssetVulMapper assetVulMapper;

    @Resource
    private AssetWebMapper assetWebMapper;


    @Override
    public IPage<AssetIp> page(IPage<AssetIp> page, QueryWrapper<AssetIp> queryWrapper, Map<String, String[]> parameterMap) {
        queryGen(queryWrapper,parameterMap);
        return super.page(page, queryWrapper);
    }

    @Override
    public List<AssetIp> list(QueryWrapper<AssetIp> queryWrapper, Map<String, String[]> parameterMap) {
        queryGen(queryWrapper,parameterMap);
        return super.list(queryWrapper);
    }

    private void queryGen(QueryWrapper<AssetIp> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("sub_domain")) {
            queryWrapper.inSql("id", "SELECT aisd.ip_id FROM asset_ip_sub_domain aisd LEFT JOIN asset_sub_domain asb ON asb.id = aisd.subdomain_id WHERE asb.sub_domain LIKE '%" + parameterMap.get("sub_domain")[0] + "%'");
        }
    }

    @Override
    public AssetIpVO convertVO(AssetIp record) {
        AssetIpVO assetIpVO = new AssetIpVO();
        BeanUtil.copyProperties(record, assetIpVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        assetIpVO.setDomainVOList(assetIpSubdomainRelationService.getAssetSubDomainByIpId(record.getId()));
        assetIpVO.setSubDomainIds(assetIpSubdomainRelationService.getSubDomainIdsByIpId(record.getId()));
        assetIpVO.setPortCount(assetPortMapper.getPortsCountByIpId(record.getId()));
        return assetIpVO;
    }

    @Override
    public AssetIpDTO convertDTO(AssetIp assetIp) {
       if(assetIp!=null){
           AssetIpDTO assetIpDTO = new AssetIpDTO();
           BeanUtil.copyProperties(assetIp, assetIpDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
           assetIpDTO.setSubDomains(assetIpSubdomainRelationService.getSubDomainsByIpId(assetIp.getId()));
           return assetIpDTO;
       }
       return null;
    }

    @Override
    public boolean addAssetByType(AssetIpDTO asset) {
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return save(assetIpSubdomainRelationService.getExtra(asset));
    }

    @Override
    public boolean updateAssetByType(AssetIpDTO asset) {
        assetIpSubdomainRelationService.delByAssetIpId(asset.getId());
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            // 删除ip和子域名的关联
            assetIpSubdomainRelationService.delByAssetIpId(id);
            // 删除端口关联的web
            List<String> portIds = assetPortMapper.getPortIdsByIpId(id);
            if (!portIds.isEmpty()) {
                // 删除端口关联的web
                assetWebMapper.deleteByPortId(portIds);
            }
            // 删除ip关联的端口
            assetPortMapper.delByIpId(id);
        });
        // 删除ip关联的漏洞
        assetVulMapper.delByIpIds(list);
        this.removeBatchByIds(list);
    }

    @Cacheable(value = "asset:ip:cache", key = "#ip + ':' + #projectId", unless = "#result == null")
    public AssetIp selectByIp(String ip, String projectId) {
        return baseMapper.selectByIp(ip, projectId);
    }

}
