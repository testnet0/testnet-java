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
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.DomainToSubdomainsAndIpsDTO;
import testnet.grpc.ClientMessageProto.ResultMessage;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DomainToSubDomainAndIPProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        DomainToSubdomainsAndIpsDTO domainToSubdomainAndIp = JSONObject.parseObject(resultBase.getResult(), DomainToSubdomainsAndIpsDTO.class);
        Result<? extends AssetBase> result = assetCommonOptionService.getAssetDOByIdAndAssetType(baseAssetId, AssetTypeEnums.DOMAIN);
        if (result.isSuccess() && result.getResult() != null) {
            AssetDomain assetDomain = (AssetDomain) result.getResult();
            domainToSubdomainAndIp.getSubDomainList().forEach(subDomain -> {
                AssetSubDomainIpsDTO oldSubDomainIpsDTO = getAssetSubDomainIpsDTO(subDomain, assetDomain);
                assetCommonOptionService.addOrUpdate(oldSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
            });
            Map<String, Object> params = new HashMap<>();
            params.put("taskName", liteFlowTask.getTaskName());
            params.put("subDomainNumber", String.valueOf(domainToSubdomainAndIp.getSubDomainList().size()));
            sysBaseAPI.sendWebHookeMessage(liteFlowTask.getTaskName(), params, "domain_subdomain_notify");
        }
    }

    private AssetSubDomainIpsDTO getAssetSubDomainIpsDTO(DomainToSubdomainsAndIpsDTO.SudDomain subDomain, AssetDomain assetDomain) {
        AssetSubDomainIpsDTO subDomainDTO = new AssetSubDomainIpsDTO();
        subDomainDTO.setProjectId(assetDomain.getProjectId());
        subDomainDTO.setDomainId(assetDomain.getId());
        subDomainDTO.setSubDomain(subDomain.getSubDomain());
        // 统一设置为A记录
        subDomainDTO.setType("A");
        subDomainDTO.setLevel(subDomain.getLevel());
        subDomainDTO.setSource(subDomain.getSource());
        subDomainDTO.setIps(subDomain.getIpList());
        return subDomainDTO;
    }
}
