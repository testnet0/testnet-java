/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.CompanyToDomainsDTO;
import testnet.grpc.ClientMessageProto.ResultMessage;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CompanyToDomainProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetCommonOptionService assetCompanyCommonOptionService;

    @Resource
    private IAssetCommonOptionService assetDomainCommonOptionService;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        CompanyToDomainsDTO domainToCompanyDTO = JSONObject.parseObject(resultBase.getResult(), CompanyToDomainsDTO.class);
        Result<? extends AssetBase> result = assetCompanyCommonOptionService.getAssetDOByIdAndAssetType(baseAssetId, AssetTypeEnums.COMPANY);
        if (result.isSuccess() && result.getResult() != null) {
            AssetCompany assetCompany = (AssetCompany) result.getResult();
            domainToCompanyDTO.getDomainList().forEach(domain -> {
                AssetDomain assetDomain = new AssetDomain();
                assetDomain.setDomain(domain.getDomain());
                assetDomain.setIcpNumber(domain.getIcp());
                assetDomain.setSource(source);
                assetDomain.setCompanyId(baseAssetId);
                assetDomain.setProjectId(assetCompany.getProjectId());
                assetDomainCommonOptionService.addOrUpdate(assetDomain, AssetTypeEnums.DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
            });
            Map<String, Object> params = new HashMap<>();
            params.put("taskName", liteFlowTask.getTaskName());
            params.put("domainNumber", domainToCompanyDTO.getDomainList().size());
            sysBaseAPI.sendWebHookeMessage(liteFlowTask.getTaskName(), params, "company_domain_notify");
        }
    }

}
