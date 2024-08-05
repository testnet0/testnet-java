/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.DomainToSubdomainsAndIpsDTO;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

@Service
@Slf4j
public class DomainToSubDomainAndIPProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase) {
        DomainToSubdomainsAndIpsDTO domainToSubdomainAndIp = JSONObject.parseObject(resultBase.getResult(), DomainToSubdomainsAndIpsDTO.class);
        AssetDomain assetDomain = assetCommonOptionService.getByIdAndAssetType(baseAssetId, AssetTypeEnums.DOMAIN);
        if (assetDomain != null) {
            domainToSubdomainAndIp.getSubDomainList().forEach(subDomain -> {
                AssetSubDomainIpsDTO oldSubDomainIpsDTO = getAssetSubDomainIpsDTO(subDomain, assetDomain);
                assetCommonOptionService.addOrUpdate(oldSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
            });
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
