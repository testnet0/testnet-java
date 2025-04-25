/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTaskAsset;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskAssetMapper;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetCommonOptionServiceImpl;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.dto.AssetApiToApiDTO;
import testnet.common.enums.AssetTypeEnums;

import testnet.grpc.ClientMessageProto.ResultMessage;

import javax.annotation.Resource;

@Service
@Slf4j
public class ApiToApiProcessor implements IAssetResultProcessorService {


    @Resource
    private AssetCommonOptionServiceImpl assetCommonOptionService;

    @Resource
    private LiteFlowTaskAssetMapper liteFlowTaskAssetMapper;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, ResultMessage resultBase) {
        AssetApiToApiDTO assetApiToApiDTO = JSONObject.parseObject(resultBase.getResult(), AssetApiToApiDTO.class);
        AssetApiDTO param = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam(), AssetApiDTO.class);
        if (assetApiToApiDTO != null && !assetApiToApiDTO.getAssetApiDTOList().isEmpty()) {
            assetApiToApiDTO.getAssetApiDTOList().forEach(
                    assetApiDTO -> {
                        AssetApiDTO dto = new AssetApiDTO();
                        BeanUtil.copyProperties(assetApiDTO, dto);
                        dto.setProjectId(param.getProjectId());
                        dto.setSource(source);
                        Result<? extends AssetBase> assetResult = assetCommonOptionService.addAssetByType(dto, AssetTypeEnums.API, false);
                        if (StringUtils.isNotBlank(liteFlowTask.getId()) && assetResult.isSuccess()) {
                            LiteFlowTaskAsset liteFlowTaskAsset = new LiteFlowTaskAsset();
                            liteFlowTaskAsset.setAssetId(assetResult.getResult().getId());
                            liteFlowTaskAsset.setLiteFlowTaskId(liteFlowTask.getId());
                            liteFlowTaskAsset.setAssetType(AssetTypeEnums.API.getCode());
                            liteFlowTaskAssetMapper.insert(liteFlowTaskAsset);
                        }
                    }
            );
        }
    }
}
