/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.dto.AssetPortDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetDomainDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetVulDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

@Service
@Slf4j
public class AssetUpdateProcessor implements IAssetResultProcessorService {


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase) {
        AssetUpdateDTO assetUpdateDTO = JSONObject.parseObject(resultBase.getResult(), AssetUpdateDTO.class);
        switch (liteFlowTask.getAssetType()) {
            case "domain":
                AssetDomainDTO newDomain = JSONObject.parseObject(assetUpdateDTO.getData(), AssetDomainDTO.class);
                assetCommonOptionService.addOrUpdate(newDomain, AssetTypeEnums.DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "sub_domain":
                AssetSubDomainIpsDTO newAssetSubDomain = JSONObject.parseObject(assetUpdateDTO.getData(), AssetSubDomainIpsDTO.class);
                assetCommonOptionService.addOrUpdate(newAssetSubDomain, AssetTypeEnums.SUB_DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "ip":
                AssetIpDTO newAssetIp = JSONObject.parseObject(assetUpdateDTO.getData(), AssetIpDTO.class);
                assetCommonOptionService.addOrUpdate(newAssetIp, AssetTypeEnums.IP, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "port":
                AssetPortDTO newAssetPort = JSONObject.parseObject(assetUpdateDTO.getData(), AssetPortDTO.class);
                assetCommonOptionService.addOrUpdate(newAssetPort, AssetTypeEnums.PORT, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "web":
                AssetWebDTO newAssetWeb = JSONObject.parseObject(assetUpdateDTO.getData(), AssetWebDTO.class);
                assetCommonOptionService.addOrUpdate(newAssetWeb, AssetTypeEnums.WEB, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "company":
                AssetCompany newAssetCompany = JSONObject.parseObject(assetUpdateDTO.getData(), AssetCompany.class);
                assetCommonOptionService.addOrUpdate(newAssetCompany, AssetTypeEnums.COMPANY, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "vul":
                AssetVulDTO newAssetVul = JSONObject.parseObject(assetUpdateDTO.getData(), AssetVulDTO.class);
                assetCommonOptionService.addOrUpdate(newAssetVul, AssetTypeEnums.COMPANY, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            case "api":
                AssetApiDTO newAssetApiDTO = JSONObject.parseObject(assetUpdateDTO.getData(), AssetApiDTO.class);
                assetCommonOptionService.addOrUpdate(newAssetApiDTO, AssetTypeEnums.API, liteFlowTask.getId(), liteFlowSubTask.getId());
                break;
            default:
                log.error("不支持的资产类型:{}", liteFlowTask.getAssetType());
        }
    }
}
