/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccake.ballcat.starter.ip2region.core.IpInfo;
import com.hccake.ballcat.starter.ip2region.searcher.Ip2regionSearcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.entity.asset.AssetIpSubDomainRelation;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.mapper.asset.AssetIpMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetIpSubdomainRelationMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSubDomainMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.vo.asset.AssetIpVO;
import org.jeecg.modules.testnet.server.vo.asset.AssetSubDomainVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testnet.common.utils.IpUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AssetIpSubDomainRelationServiceImpl extends ServiceImpl<AssetIpSubdomainRelationMapper, AssetIpSubDomainRelation> implements IAssetIpSubdomainRelationService {

    @Resource
    private AssetIpMapper assetIpMapper;
    @Resource
    private AssetSubDomainMapper assetSubDomainMapper;

    @Resource
    private Ip2regionSearcher ip2regionService;


    @Override
    @Transactional
    public void addDomainRelation(AssetSubDomainIpsDTO domainToSubdomainAndIpDTO) {
        if (domainToSubdomainAndIpDTO.getIps() != null) {
            String[] ipArr = domainToSubdomainAndIpDTO.getIps().split(",");
            for (String ip : ipArr) {
                ip = ip.trim();
                if (StringUtils.isBlank(ip)) {
                    continue;
                }
                AssetIp assetIp = assetIpMapper.selectByIp(ip, domainToSubdomainAndIpDTO.getProjectId());
                AssetIpSubDomainRelation assetIpSubDomainRelation = new AssetIpSubDomainRelation();
                assetIpSubDomainRelation.setSubdomainId(domainToSubdomainAndIpDTO.getId());
                if (assetIp == null) {
                    AssetIpDTO assetIpDTO = new AssetIpDTO();
                    assetIpDTO.setIp(ip);
                    assetIpDTO.setProjectId(domainToSubdomainAndIpDTO.getProjectId());
                    assetIpDTO.setSource(domainToSubdomainAndIpDTO.getSource());
                    assetIpDTO = getExtra(assetIpDTO);
                    assetIpMapper.insert(assetIpDTO);
                    assetIpSubDomainRelation.setIpId(assetIpDTO.getId());
                    save(assetIpSubDomainRelation);
                } else {
                    assetIpSubDomainRelation.setIpId(assetIp.getId());
                    if (isNotExists(assetIpSubDomainRelation)) {
                        save(assetIpSubDomainRelation);
                    }
                }
            }
        }
    }

    private boolean isNotExists(AssetIpSubDomainRelation assetIpSubDomain) {
        LambdaQueryWrapper<AssetIpSubDomainRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetIpSubDomainRelation::getIpId, assetIpSubDomain.getIpId());
        queryWrapper.eq(AssetIpSubDomainRelation::getSubdomainId, assetIpSubDomain.getSubdomainId());
        return count(queryWrapper) == 0;
    }


    @Override
    public void delByAssetIpId(String ipId) {
        LambdaQueryWrapper<AssetIpSubDomainRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetIpSubDomainRelation::getIpId, ipId);
        remove(queryWrapper);
    }

    @Override
    public void delByAssetSubDomainId(String subDomainId) {
        LambdaQueryWrapper<AssetIpSubDomainRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetIpSubDomainRelation::getSubdomainId, subDomainId);
        remove(queryWrapper);
    }


    @Override
    public String getSubDomainIdsByIpId(String ipId) {
        List<AssetIpSubDomainRelation> assetIpSubDomainRelations = getByAssetIpId(ipId);
        if (assetIpSubDomainRelations != null && !assetIpSubDomainRelations.isEmpty()) {
            return assetIpSubDomainRelations.stream().map(AssetIpSubDomainRelation::getSubdomainId).collect(Collectors.joining(","));
        } else {
            return "";
        }
    }

    @Override
    public String getIpIdsBySubDomainId(String subDomainId) {
        List<AssetIpSubDomainRelation> assetIpSubDomainRelations = getByAssetSubDomainId(subDomainId);
        if (assetIpSubDomainRelations != null && !assetIpSubDomainRelations.isEmpty()) {
            return assetIpSubDomainRelations.stream().map(AssetIpSubDomainRelation::getIpId).collect(Collectors.joining(","));
        } else {
            return "";
        }
    }

    @Override
    public String getSubDomainsByIpId(String ipId) {
        List<AssetIpSubDomainRelation> assetIpSubDomainRelations = getByAssetIpId(ipId);
        if (assetIpSubDomainRelations != null && !assetIpSubDomainRelations.isEmpty()) {
            StringBuilder subdomains = new StringBuilder();
            List<AssetSubDomain> assetSubDomains = assetSubDomainMapper.selectBatchIds(assetIpSubDomainRelations.stream().map(AssetIpSubDomainRelation::getSubdomainId).collect(Collectors.toList()));
            assetSubDomains.forEach(assetSubDomain -> {
                if (subdomains.length() > 0) {
                    subdomains.append(",");
                }
                subdomains.append(assetSubDomain.getSubDomain());
            });
            return subdomains.toString();
        }
        return "";
    }

    @Override
    public String getIpsBySubDomainId(String subDomainId) {
        List<AssetIpSubDomainRelation> assetIpSubDomainRelations = getByAssetSubDomainId(subDomainId);
        if (assetIpSubDomainRelations != null && !assetIpSubDomainRelations.isEmpty()) {
            StringBuilder ips = new StringBuilder();
            List<AssetIp> assetIps = assetIpMapper.selectBatchIds(assetIpSubDomainRelations.stream().map(AssetIpSubDomainRelation::getIpId).collect(Collectors.toList()));
            assetIps.forEach(assetIp -> {
                ips.append(assetIp.getIp());
                ips.append(",");
            });
            ips.deleteCharAt(ips.length() - 1);
            return ips.toString();
        }
        return "";
    }


    @Override
    public List<AssetIpSubDomainRelation> getByAssetIpId(String ipId) {
        LambdaQueryWrapper<AssetIpSubDomainRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetIpSubDomainRelation::getIpId, ipId);
        return list(queryWrapper);
    }

    @Override
    public List<AssetIpSubDomainRelation> getByAssetSubDomainId(String subDomainId) {
        LambdaQueryWrapper<AssetIpSubDomainRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssetIpSubDomainRelation::getSubdomainId, subDomainId);
        return list(queryWrapper);
    }

    @Override
    public AssetIpDTO getExtra(AssetIpDTO assetIpDTO) {
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

    @Override
    public List<AssetIpVO> getAssetIpBySubDomainId(String subDomainId) {
        return assetIpMapper.getBySubDomainId(subDomainId);
    }

    @Override
    public List<AssetSubDomainVO> getAssetSubDomainByIpId(String ipId) {
        return assetSubDomainMapper.getSubDomainListByIpId(ipId);
    }

    @Override
    public void addDomainRelation(AssetIpDTO asset) {
        AssetIpSubDomainRelation assetIpSubDomainRelation = new AssetIpSubDomainRelation();
        assetIpSubDomainRelation.setIpId(asset.getId());
        if (StringUtils.isNotBlank(asset.getSubDomainIds())) {
            String[] domains = asset.getSubDomainIds().split(",");
            for (String subDomainId : domains) {
                assetIpSubDomainRelation.setSubdomainId(subDomainId);
                if (isNotExists(assetIpSubDomainRelation)) {
                    save(assetIpSubDomainRelation);
                }
            }
        }
    }

}
