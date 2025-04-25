package org.jeecg.modules.testnet.server.service.asset.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.dto.asset.AssetVulDTO;
import org.jeecg.modules.testnet.server.entity.asset.*;
import org.jeecg.modules.testnet.server.mapper.asset.AssetIpMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSubDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetVulMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.vo.asset.AssetVulVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 漏洞
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetVulServiceImpl extends ServiceImpl<AssetVulMapper, AssetVul> implements IAssetService<AssetVul, AssetVulVO, AssetVulDTO> {

    @Resource
    private AssetSubDomainMapper assetSubDomainMapper;

    @Resource
    private AssetIpMapper assetIpMapper;

    @Resource
    private AssetWebMapper assetWebMapper;

    @Resource
    private AssetPortServiceImpl assetPortService;

    @Override
    public IPage<AssetVul> page(IPage<AssetVul> page, QueryWrapper<AssetVul> queryWrapper, Map<String, String[]> parameterMap) {
        return super.page(page, queryWrapper);
    }

    @Override
    public List<AssetVul> list(QueryWrapper<AssetVul> queryWrapper, Map<String, String[]> parameterMap) {
        return super.list(queryWrapper);
    }

    @Override
    public AssetVulVO convertVO(AssetVul record) {
        if (record != null) {
            AssetVulVO assetVulVO = new AssetVulVO();
            switch (record.getAssetType()) {
                case "sub_domain":
                    assetVulVO.setSubDomainId(record.getAssetId());
                    break;
                case "web":
                    assetVulVO.setWebId(record.getAssetId());
                    break;
                case "ip":
                    assetVulVO.setIpId(record.getAssetId());
                    break;
                case "port":
                    AssetPort assetPort = assetPortService.getById(record.getAssetId());
                    if (assetPort != null) {
                        assetVulVO.setPortId(record.getAssetId());
                        assetVulVO.setIpId(assetPort.getIp());
                    }
                    break;
            }
            BeanUtil.copyProperties(record, assetVulVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            return assetVulVO;
        }
        return null;
    }

    @Override
    public AssetVulDTO convertDTO(AssetVul asset) {
        if (asset != null) {
            AssetVulDTO assetVulDTO = new AssetVulDTO();
            BeanUtil.copyProperties(asset, assetVulDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            return assetVulDTO;
        }
        return null;
    }

    @Override
    public boolean addAssetByType(AssetVulDTO asset) {
        switch (asset.getAssetType()) {
            case "sub_domain":
                AssetSubDomain assetSubDomain = assetSubDomainMapper.selectById(asset.getAssetId());
                if (assetSubDomain != null) {
                    asset.setProjectId(assetSubDomain.getProjectId());
                }
                break;
            case "web":
                AssetWeb assetWeb = assetWebMapper.selectById(asset.getAssetId());
                if (assetWeb != null) {
                    asset.setProjectId(assetWeb.getProjectId());
                }
                break;
            case "ip":
                AssetIp assetIp = assetIpMapper.selectById(asset.getAssetId());
                if (assetIp != null) {
                    asset.setProjectId(assetIp.getProjectId());
                }
                break;
            case "port":
                AssetPort assetPort = assetPortService.getById(asset.getAssetId());
                if (assetPort != null) {
                    asset.setProjectId(assetPort.getProjectId());
                }
                break;
        }
        return save(asset);
    }

    @Override
    public boolean updateAssetByType(AssetVulDTO asset) {
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(this::removeById);
    }


    @Override
    public List<AssetVul> list(Wrapper<AssetVul> queryWrapper) {
        List<AssetVul> assetVulList = this.getBaseMapper().selectList(queryWrapper);
        List<AssetVul> newAssetVulList = new ArrayList<>();
        for (AssetVul assetVul : assetVulList) {
            switch (assetVul.getAssetType()) {
                case "sub_domain":
                    AssetSubDomain assetSubdomain = assetSubDomainMapper.selectById(assetVul.getAssetId());
                    assetVul.setAssetId(assetSubdomain.getSubDomain());
                    newAssetVulList.add(assetVul);
                    break;
                case "web":
                    AssetWeb assetWeb = assetWebMapper.selectById(assetVul.getAssetId());
                    assetVul.setAssetId(assetWeb.getWebUrl());
                    newAssetVulList.add(assetVul);
                    break;
                case "ip":
                    assetVul.setAssetId(assetIpMapper.selectById(assetVul.getAssetId()).getIp());
                    newAssetVulList.add(assetVul);
                    break;
                case "port":
                    AssetPort assetPort = assetPortService.getById(assetVul.getAssetId());
                    if (assetPort != null) {
                        AssetIp assetIp = assetIpMapper.selectById(assetPort.getIp());
                        assetVul.setAssetId(assetIp.getIp() + ":" + assetPort.getPort());
                    }
                    newAssetVulList.add(assetVul);
                    break;
            }
        }
        return newAssetVulList;
    }

    public void changeVulStatus(String id, String vulStatus) {
        if (StringUtils.isNotBlank(id)) {
            AssetVul assetVul = getById(id);
            if (assetVul != null) {
                assetVul.setVulStatus(vulStatus);
                updateById(assetVul);
            }
        }
    }
}
