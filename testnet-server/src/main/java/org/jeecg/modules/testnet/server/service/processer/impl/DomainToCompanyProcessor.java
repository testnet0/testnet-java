/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.DomainToCompanyDTO;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

@Slf4j
@Service
public class DomainToCompanyProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase) {
        DomainToCompanyDTO domainToCompanyDTO = JSONObject.parseObject(resultBase.getResult(), DomainToCompanyDTO.class);
        if (domainToCompanyDTO != null) {
            AssetDomain assetDomain = assetCommonOptionService.getByIdAndAssetType(baseAssetId, AssetTypeEnums.DOMAIN);
            if (domainToCompanyDTO.getIcpNumber() != null && StringUtils.isNotBlank(domainToCompanyDTO.getIcpNumber())) {
                assetDomain.setIcpNumber(domainToCompanyDTO.getIcpNumber());
            }
            AssetCompany assetCompany = new AssetCompany();
            assetCompany.setCompanyName(domainToCompanyDTO.getCompanyName());
            assetCompany.setSource(source);
            assetCompany.setProjectId(assetDomain.getProjectId());
            assetCompany = assetCommonOptionService.addOrUpdate(assetCompany, AssetTypeEnums.COMPANY, liteFlowTask.getId(), liteFlowSubTask.getId());
            if (assetCompany != null && StringUtils.isNotBlank(assetCompany.getId())) {
                assetDomain.setCompanyId(assetCompany.getId());
                assetCommonOptionService.addOrUpdate(assetDomain, AssetTypeEnums.DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
            }
        } else {
            log.error("数据为空！");
        }
    }
}
