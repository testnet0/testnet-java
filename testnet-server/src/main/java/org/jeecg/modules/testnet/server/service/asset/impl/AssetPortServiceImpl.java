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
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.asset.AssetPortVO;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
    private IAssetValidService assetValidService;

    @Resource
    private AssetWebServiceImpl assetWebService;

    @Resource
    private AssetIpMapper assetIpMapper;


    @Override
    public IPage<AssetPort> page(IPage<AssetPort> page, QueryWrapper<AssetPort> queryWrapper, Map<String, String[]> parameterMap) {
        return super.page(page, queryWrapper);
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
        list.forEach(id -> {
            List<String> assetWebIds = assetWebService.getByPortId(id);
            assetWebService.delRelation(assetWebIds);
            removeById(id);
        });
    }

    @Override
    public boolean saveBatch(Collection<AssetPort> entityList) {
        List<AssetPort> assetPortList = new ArrayList<>();
        for (AssetPort assetPort : entityList) {
            if (assetValidService.isValid(assetPort, AssetTypeEnums.PORT)) {
                if (assetValidService.getUniqueAsset(assetPort, this, AssetTypeEnums.PORT) == null) {
                    assetPortList.add(assetPort);
                } else {
                    log.info("端口:{} 重复，跳过", assetPort);
                }
            }
        }
        return super.saveBatch(assetPortList);
    }

}
