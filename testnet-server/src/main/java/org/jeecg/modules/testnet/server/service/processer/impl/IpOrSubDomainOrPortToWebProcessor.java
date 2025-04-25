/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetPortDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.IpOrSubDomainOrPortToWebDTO;
import testnet.grpc.ClientMessageProto.ResultMessage;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
@Slf4j
public class IpOrSubDomainOrPortToWebProcessor implements IAssetResultProcessorService {


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        IpOrSubDomainOrPortToWebDTO ipOrSubDomainOrPortToWeb = JSONObject.parseObject(resultBase.getResult(), IpOrSubDomainOrPortToWebDTO.class);
        String projectId = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam()).getString("projectId");
        AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
        Result<? extends AssetBase> assetSubDomainResult = new Result<>();
        if (StringUtils.isNotBlank(ipOrSubDomainOrPortToWeb.getDomain())) {
            assetSubDomainIpsDTO.setSubDomain(ipOrSubDomainOrPortToWeb.getDomain());
            assetSubDomainIpsDTO.setIps(ipOrSubDomainOrPortToWeb.getIp());
            assetSubDomainIpsDTO.setProjectId(projectId);
            assetSubDomainResult = assetCommonOptionService.addOrUpdate(assetSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
        }
        HashMap<String, String> fieldMap = new HashMap<>();
        fieldMap.put("ip", ipOrSubDomainOrPortToWeb.getIp());
        fieldMap.put("project_id", projectId);
        Result<? extends AssetBase> assetIpResult = assetCommonOptionService.getByFieldAndAssetType(fieldMap, AssetTypeEnums.IP);
        if (assetIpResult.isSuccess() && assetIpResult.getResult() != null) {
            AssetPortDTO assetPort = new AssetPortDTO();
            assetPort.setPort(Integer.valueOf(ipOrSubDomainOrPortToWeb.getPort()));
            assetPort.setIsWeb("Y");
            assetPort.setIsOpen("Y");
            assetPort.setProjectId(assetIpResult.getResult().getProjectId());
            assetPort.setIp(assetIpResult.getResult().getId());
            Result<? extends AssetBase> assetPortResult = assetCommonOptionService.addOrUpdate(assetPort, AssetTypeEnums.PORT, liteFlowTask.getId(), liteFlowSubTask.getId());
            if (assetPortResult.isSuccess() && assetPortResult.getResult() != null) {
                AssetWebDTO assetWeb = new AssetWebDTO();
                BeanUtil.copyProperties(ipOrSubDomainOrPortToWeb, assetWeb, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                assetWeb.setPortId(assetPortResult.getResult().getId());
                if (assetSubDomainResult.isSuccess() && assetSubDomainResult.getResult() != null) {
                    assetWeb.setDomain(assetSubDomainResult.getResult().getId());
                } else {
                    assetWeb.setDomain("");
                }
                assetWeb.setProjectId(assetIpResult.getResult().getProjectId());
                assetWeb.setBody(ipOrSubDomainOrPortToWeb.getResponseBody());
                assetCommonOptionService.addOrUpdate(assetWeb, AssetTypeEnums.WEB, liteFlowTask.getId(), liteFlowSubTask.getId());
            }
        }
    }
}
