package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.AssetPortDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;
import org.jeecg.modules.testnet.server.mapper.asset.AssetIpMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetPortMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.vo.asset.AssetPortVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 端口
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetPortServiceImpl extends ServiceImpl<AssetPortMapper, AssetPort> implements IAssetService<AssetPort, AssetPortVO, AssetPortDTO> {

    @Resource
    private IAssetIpSubdomainRelationService assetIpSubdomainRelationService;


    @Resource
    private AssetWebMapper assetWebMapper;

    @Resource
    private AssetIpMapper assetIpMapper;


    @Override
    public IPage<AssetPort> page(IPage<AssetPort> page, QueryWrapper<AssetPort> queryWrapper, Map<String, String[]> parameterMap) {
        queryGen(queryWrapper, parameterMap);
        return super.page(page, queryWrapper);
    }

    @Override
    public List<AssetPort> list(QueryWrapper<AssetPort> queryWrapper, Map<String, String[]> parameterMap) {
        queryGen(queryWrapper, parameterMap);
        return super.list(queryWrapper);
    }

    private void queryGen(QueryWrapper<AssetPort> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("subdomain")) {
            queryWrapper.inSql("ip", "SELECT aisd.ip_id FROM asset_ip_sub_domain aisd LEFT JOIN asset_sub_domain asd ON asd.id = aisd.subdomain_id WHERE asd.sub_domain LIKE '%" + parameterMap.get("subdomain")[0] + "%'");
        }
        if (parameterMap != null && parameterMap.containsKey("ips")) {
            queryWrapper.inSql("ip", "select id from asset_ip where ip like '%" + parameterMap.get("ips")[0] + "%'");
        }
    }

    @Override
    public AssetPortVO convertVO(AssetPort record) {
        AssetPortVO assetPortVO = new AssetPortVO();
        BeanUtil.copyProperties(record, assetPortVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        assetPortVO.setDomains(assetIpSubdomainRelationService.getAssetSubDomainByIpId(record.getIp()));
        return assetPortVO;
    }

    @Override
    public AssetPortDTO convertDTO(AssetPort asset) {
        if (asset != null) {
            AssetPortDTO assetPortDTO = new AssetPortDTO();
            BeanUtil.copyProperties(asset, assetPortDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            assetPortDTO.setDomains(assetIpSubdomainRelationService.getAssetSubDomainByIpId(assetPortDTO.getIp()));
            AssetIp assetIp = assetIpMapper.selectById(assetPortDTO.getIp());
            if (assetIp != null) {
                assetPortDTO.setIp_dictText(assetIp.getIp());
            }
            return assetPortDTO;
        }
        return null;
    }

    @Override
    public boolean addAssetByType(AssetPortDTO asset) {
        return save(asset);
    }

    @Override
    public boolean updateAssetByType(AssetPortDTO asset) {
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        assetWebMapper.deleteByPortId(list);
    }

    @Cacheable(value = "asset:port:cache", key = "#id + ':' + #port", unless = "#result == null")
    public AssetPort getPortByIpIdAndPort(String id, int port) {
        return getOne(new QueryWrapper<AssetPort>().eq("ip", id).eq("port", port));
    }

}
