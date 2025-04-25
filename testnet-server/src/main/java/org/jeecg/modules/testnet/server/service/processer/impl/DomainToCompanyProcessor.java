/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.DomainToCompanyDTO;
import testnet.grpc.ClientMessageProto.ResultMessage;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

@Slf4j
@Service
public class DomainToCompanyProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetCommonOptionService assetDomainCommonOptionService;

    @Resource
    private IAssetCommonOptionService assetCompanyCommonOptionService;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        DomainToCompanyDTO domainToCompanyDTO = JSONObject.parseObject(resultBase.getResult(), DomainToCompanyDTO.class);
        if (domainToCompanyDTO != null) {
            Result<? extends AssetBase> result = assetDomainCommonOptionService.getAssetDOByIdAndAssetType(baseAssetId, AssetTypeEnums.DOMAIN);
            if (result.isSuccess() && result.getResult() != null) {
                AssetDomain assetDomain = (AssetDomain) result.getResult();
                if (domainToCompanyDTO.getIcpNumber() != null && StringUtils.isNotBlank(domainToCompanyDTO.getIcpNumber())) {
                    assetDomain.setIcpNumber(domainToCompanyDTO.getIcpNumber());
                }
                AssetCompany assetCompany = new AssetCompany();
                assetCompany.setCompanyName(domainToCompanyDTO.getCompanyName());
                assetCompany.setSource(source);
                assetCompany.setProjectId(assetDomain.getProjectId());
                Result<? extends AssetBase> assetCompanyResult = assetCompanyCommonOptionService.addOrUpdate(assetCompany, AssetTypeEnums.COMPANY, liteFlowTask.getId(), liteFlowSubTask.getId());
                if (assetCompanyResult.isSuccess() && assetCompanyResult.getResult() != null) {
                    assetDomain.setCompanyId(assetCompanyResult.getResult().getId());
                }
                assetDomainCommonOptionService.addOrUpdate(assetDomain, AssetTypeEnums.DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
            }
        } else {
            log.error("数据为空！");
        }
    }
}
