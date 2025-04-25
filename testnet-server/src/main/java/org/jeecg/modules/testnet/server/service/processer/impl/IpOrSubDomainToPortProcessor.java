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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.dto.AssetPortDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.IpOrSubDomainToPortDTO;
import testnet.grpc.ClientMessageProto.ResultMessage;

import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IpOrSubDomainToPortProcessor implements IAssetResultProcessorService {


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        IpOrSubDomainToPortDTO dto = JSONObject.parseObject(resultBase.getResult(), IpOrSubDomainToPortDTO.class);
        List<IpOrSubDomainToPortDTO.Port> portList = dto.getPortList();
        String projectId = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam()).getString("projectId");
        // 先判断ip是否存在（缓存），再判断域名ip关系是否存在（缓存），再直接更新端口信息
        if (portList != null && !portList.isEmpty()) {
            portList.forEach(port -> {
                AssetPortDTO assetPortDTO = new AssetPortDTO();
                // 如果是子域名的端口扫描 需要先将ip存入数据库
                if (liteFlowTask.getAssetType().equals(AssetTypeEnums.SUB_DOMAIN.getCode())) {
                    AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
                    assetSubDomainIpsDTO.setSubDomain(port.getHost());
                    assetSubDomainIpsDTO.setProjectId(projectId);
                    assetSubDomainIpsDTO.setIps(port.getIp());
                    assetSubDomainIpsDTO.setSource(source);
                    assetCommonOptionService.addOrUpdate(assetSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, liteFlowTask.getId(), liteFlowSubTask.getId());
                }
                if(liteFlowTask.getAssetType().equals(AssetTypeEnums.IP.getCode())){
                    AssetIpDTO assetIpDTO = new AssetIpDTO();
                    assetIpDTO.setIp(port.getIp());
                    assetIpDTO.setProjectId(projectId);
                    assetIpDTO.setSource(source);
                    assetCommonOptionService.addOrUpdate(assetIpDTO, AssetTypeEnums.IP, liteFlowTask.getId(), liteFlowSubTask.getId());
                }

                HashMap<String, String> fieldMap = new HashMap<>();
                fieldMap.put("ip", port.getIp());
                fieldMap.put("project_id", projectId);
                Result<?> result = assetCommonOptionService.getByFieldAndAssetType(fieldMap, AssetTypeEnums.IP);
                if (result.isSuccess() && result.getResult() != null) {
                    AssetIpDTO assetIpDTO = (AssetIpDTO) result.getResult();
                    BeanUtil.copyProperties(port, assetPortDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                    assetPortDTO.setProjectId(assetIpDTO.getProjectId());
                    assetPortDTO.setIp(assetIpDTO.getId());
                    assetPortDTO.setPort(port.getPort());
                    assetPortDTO.setIsOpen("Y");
                    assetPortDTO.setSource(source);
                    assetPortDTO.setService(port.getService());
                    assetCommonOptionService.addOrUpdate(assetPortDTO, AssetTypeEnums.PORT, liteFlowTask.getId(), liteFlowSubTask.getId());
                } else {
                    log.error("IP:{}不存在", port.getIp());
                }
            });
            Map<String, Object> params = new HashMap<>();
            params.put("taskName", liteFlowTask.getTaskName());
            params.put("portNumber", String.valueOf(portList.size()));
            sysBaseAPI.sendWebHookeMessage(liteFlowTask.getTaskName(), params, "port_notify");
        }

    }
}
