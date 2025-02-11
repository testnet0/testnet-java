package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.mapper.asset.AssetCompanyMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetDomainMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.asset.AssetCompanyVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 公司
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetCompanyServiceImpl extends ServiceImpl<AssetCompanyMapper, AssetCompany> implements IAssetService<AssetCompany, AssetCompanyVO, AssetCompany> {

    @Resource
    private AssetDomainMapper assetDomainMapper;

    @Override
    public IPage<AssetCompany> page(IPage<AssetCompany> page, QueryWrapper<AssetCompany> queryWrapper, Map<String, String[]> parameterMap) {
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetCompanyVO convertVO(AssetCompany record) {
        AssetCompanyVO assetCompanyVO = new AssetCompanyVO();
        BeanUtil.copyProperties(record, assetCompanyVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        return assetCompanyVO;
    }

    @Override
    public AssetCompany convertDTO(AssetCompany record) {
        return record;
    }

    @Override
    public boolean addAssetByType(AssetCompany asset) {
        return save(asset);
    }

    @Override
    public boolean updateAssetByType(AssetCompany asset) {
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            // 删除域名和公司关联
            assetDomainMapper.removeCompanyRelation(id);
        });
        this.removeBatchByIds(list);
    }

}
