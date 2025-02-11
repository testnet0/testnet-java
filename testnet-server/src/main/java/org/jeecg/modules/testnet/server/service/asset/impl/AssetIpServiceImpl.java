package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccake.ballcat.starter.ip2region.core.IpInfo;
import com.hccake.ballcat.starter.ip2region.searcher.Ip2regionSearcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.mapper.asset.AssetIpMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetPortMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetVulMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.asset.AssetIpVO;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.utils.IpUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
    private IAssetValidService assetValidService;

    @Resource
    private Ip2regionSearcher ip2regionService;

    @Resource
    private AssetPortMapper assetPortMapper;

    @Resource
    private AssetPortServiceImpl assetPortService;

    @Resource
    private AssetVulMapper assetVulMapper;


    @Override
    public IPage<AssetIp> page(IPage<AssetIp> page, QueryWrapper<AssetIp> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("sub_domain_id")) {
            String domainId = parameterMap.get("sub_domain_id")[0];
            queryWrapper.in("id", assetIpSubdomainRelationService.getIpIdsBySubDomainId(domainId));
        }
        return super.page(page, queryWrapper);
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
        AssetIpDTO assetIpDTO = new AssetIpDTO();
        BeanUtil.copyProperties(assetIp, assetIpDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        assetIpDTO.setSubDomainIds(assetIpSubdomainRelationService.getSubDomainsByIpId(assetIp.getId()));
        return assetIpDTO;
    }

    @Override
    public boolean addAssetByType(AssetIpDTO asset) {
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return save(getExtra(asset));
    }

    @Override
    public boolean updateAssetByType(AssetIpDTO asset) {
        // 这里目前没有从IP获取域名的情况 所以只有手工编辑
        assetIpSubdomainRelationService.delByAssetIpId(asset.getId());
        assetIpSubdomainRelationService.addDomainRelation(asset);
        return updateById(getExtra(asset));
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            assetIpSubdomainRelationService.delByAssetIpId(id);
            assetVulMapper.delByIpId(id);
            List<String> portIds = assetPortMapper.getPortIdsByIpId(id);
            assetPortService.delRelation(portIds);
            removeById(id);
        });
    }

    @Override
    public boolean saveBatch(Collection<AssetIp> entityList) {
        List<AssetIp> assetIpList = new ArrayList<>();
        for (AssetIp assetIp : entityList) {
            if (assetValidService.isValid(assetIp, AssetTypeEnums.IP)) {
                if (assetValidService.getUniqueAsset(assetIp, this, AssetTypeEnums.IP) == null) {
                    assetIpList.add(assetIp);
                } else {
                    log.info("ip:{} 重复,跳过", assetIp.getIp());
                }
            }
        }
        return super.saveBatch(assetIpList);
    }

    private AssetIpDTO getExtra(AssetIpDTO assetIpDTO) {
        if (StringUtils.isNotBlank(assetIpDTO.getIp())) {
            IpInfo address = ip2regionService.search(assetIpDTO.getIp());
            if (address != null) {
                BeanUtil.copyProperties(address, assetIpDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            }
        }
        if (IpUtils.isIpv6(assetIpDTO.getIp())) {
            assetIpDTO.setIsIpv6("Y");
        } else {
            assetIpDTO.setIsIpv6("N");
        }
        if (IpUtils.isPrivateIP(assetIpDTO.getIp())) {
            assetIpDTO.setIsPublic("N");
        } else {
            assetIpDTO.setIsPublic("Y");
        }
        return assetIpDTO;
    }

}
