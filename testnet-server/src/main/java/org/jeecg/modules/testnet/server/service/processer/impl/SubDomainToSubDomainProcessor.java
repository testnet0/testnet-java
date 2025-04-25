/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.DomainToSubdomainsAndIpsDTO;
import testnet.common.enums.AssetTypeEnums;
import testnet.grpc.ClientMessageProto.ResultMessage;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SubDomainToSubDomainProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        DomainToSubdomainsAndIpsDTO domainToSubdomainAndIp = JSONObject.parseObject(resultBase.getResult(), DomainToSubdomainsAndIpsDTO.class);
        Result<? extends AssetBase> result = assetCommonOptionService.getAssetDOByIdAndAssetType(baseAssetId, AssetTypeEnums.SUB_DOMAIN);
        if (result.isSuccess() && result.getResult() != null) {
            AssetSubDomain assetSubDomain = (AssetSubDomain) result.getResult();
            domainToSubdomainAndIp.getSubDomainList().forEach(subDomain -> {
                AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
                assetSubDomainIpsDTO.setDomainId(assetSubDomain.getDomainId());
                assetSubDomainIpsDTO.setIps(subDomain.getIpList());
                assetSubDomainIpsDTO.setSubDomain(subDomain.getSubDomain());
                assetSubDomainIpsDTO.setProjectId(assetSubDomain.getProjectId());
                assetSubDomainIpsDTO.setSource(subDomain.getSource());
                assetCommonOptionService.addOrUpdate(assetSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
            });
            Map<String, Object> params = new HashMap<>();
            params.put("taskName", liteFlowTask.getTaskName());
            params.put("subDomainNumber", String.valueOf(domainToSubdomainAndIp.getSubDomainList().size()));
            sysBaseAPI.sendWebHookeMessage(liteFlowTask.getTaskName(), params, "domain_subdomain_notify");
        }
    }
}
