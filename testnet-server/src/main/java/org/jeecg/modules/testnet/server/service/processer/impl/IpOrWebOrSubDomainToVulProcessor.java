/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.dto.asset.AssetVulDTO;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.IpOrWebOrSubDomainToVulDTO;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;

@Slf4j
@Service
public class IpOrWebOrSubDomainToVulProcessor implements IAssetResultProcessorService {
    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase) {
        IpOrWebOrSubDomainToVulDTO ipOrSubDomainOrPortToWeb = JSONObject.parseObject(resultBase.getResult(), IpOrWebOrSubDomainToVulDTO.class);
        if (ipOrSubDomainOrPortToWeb != null && ipOrSubDomainOrPortToWeb.getAssetVulList() != null) {
            String instanceParams = liteFlowSubTask.getSubTaskParam();
            String projectId = JSONObject.parseObject(instanceParams).getString("projectId");
            for (IpOrWebOrSubDomainToVulDTO.AssetVul assetVul : ipOrSubDomainOrPortToWeb.getAssetVulList()) {
                AssetVulDTO vul = new AssetVulDTO();
                BeanUtil.copyProperties(assetVul, vul, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                vul.setProjectId(projectId);
                vul.setAssetId(baseAssetId);
                vul.setSource(source);
                vul.setAssetType(liteFlowTask.getAssetType());
                vul.setVulStatus("0");
                assetCommonOptionService.addOrUpdate(vul, AssetTypeEnums.VUL, liteFlowTask.getId(), liteFlowSubTask.getId());
            }
        }
    }
}