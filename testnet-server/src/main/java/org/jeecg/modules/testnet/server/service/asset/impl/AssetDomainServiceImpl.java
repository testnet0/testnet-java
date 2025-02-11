package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.asset.AssetIpSubDomainRelation;
import org.jeecg.modules.testnet.server.mapper.asset.AssetCompanyMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetSubDomainMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.vo.asset.AssetDomainVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private AssetCompanyMapper assetCompanyMapper;

    @Resource
    private AssetWebMapper assetWebMapper;

    @Resource
    private AssetIpSubDomainRelationServiceImpl assetIpSubDomainRelationService;

    @Override
    public IPage<AssetDomain> page(IPage<AssetDomain> page, QueryWrapper<AssetDomain> queryWrapper, Map<String, String[]> parameterMap) {
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetDomainVO convertVO(AssetDomain record) {
        AssetDomainVO assetDomainVO = new AssetDomainVO();
        BeanUtil.copyProperties(record, assetDomainVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        assetDomainVO.setSubDomainNumber(assetSubDomainMapper.getSubDomainCountByDomain(record.getId()));
        if (StringUtils.isNotBlank(record.getCompanyId())) {
            AssetCompany assetCompany = assetCompanyMapper.selectById(record.getCompanyId());
            if (assetCompany != null) {
                assetDomainVO.setAssetCompanyLabel(assetCompany.getAssetLabel());
            }
        }
        return assetDomainVO;
    }

    @Override
    public AssetDomain convertDTO(AssetDomain asset) {
        return asset;
    }

    @Override
    public boolean addAssetByType(AssetDomain asset) {
        processCompany(asset);
        return save(asset);
    }

    @Override
    public boolean updateAssetByType(AssetDomain asset) {
        processCompany(asset);
        return updateById(asset);
    }

    private void processCompany(AssetDomain assetDomain) {
        if (StringUtils.isBlank(assetDomain.getCompanyId())) {
            return;
        }
        AssetCompany assetCompany = assetCompanyMapper.getCompanyByIdORName(assetDomain.getCompanyId());
        if (assetCompany == null) {
            assetCompany = new AssetCompany();
            assetCompany.setCompanyName(assetDomain.getCompanyId());
            assetCompany.setSource(assetDomain.getSource());
            assetCompany.setProjectId(assetDomain.getProjectId());
            assetCompanyMapper.insert(assetCompany);
        }
        assetDomain.setCompanyId(assetCompany.getId());
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            // 获取所有关联的子域名ID
            List<String> subDomainIds = assetSubDomainMapper.getSubDomainIdsListByDomain(id);
            // 删除每个子域名关联的IP和Web记录
            subDomainIds.forEach(subDomainId -> {
                assetIpSubDomainRelationService.delByAssetSubDomainId(subDomainId);
                assetWebMapper.deleteBySubDomainId(subDomainId);
            });
            // 删除所有关联的子域名
            assetSubDomainMapper.deleteByDomainId(id);
        });
        this.removeBatchByIds(list);
    }
}
