package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.dto.AssetSearchImportDTO;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchService;
import org.jeecg.modules.testnet.server.service.log.ILogService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.jeecg.modules.testnet.server.vo.AssetSearchVO;
import org.springframework.stereotype.Service;
import testnet.common.entity.liteflow.LiteFlowResult;

import javax.annotation.Resource;

@Service
@Slf4j
public class SearchEngineImportProcessor implements IAssetResultProcessorService {

    @Resource
    private IAssetSearchService assetSearchService;

    @Resource
    private ILogService logService;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase) {
        JSONObject param = JSONObject.parseObject(resultBase.getResult());
        JSONObject searchParam = JSONObject.parseObject(liteFlowTask.getSearchParam());
        AssetSearchDTO assetSearchDTO = JSONObject.parseObject(searchParam.getString("params"), AssetSearchDTO.class);
        assetSearchDTO.setPageNo(param.getInteger("currentPage"));
        Result<IPage<AssetSearchVO>> result = assetSearchService.list(assetSearchDTO);
        if (result.isSuccess()) {
            logService.addINFOLog("server", result.getMessage(), liteFlowSubTask.getId());
            IPage<AssetSearchVO> data = result.getResult();
            AssetSearchImportDTO assetSearchImportDTO = new AssetSearchImportDTO();
            assetSearchImportDTO.setData(data.getRecords());
            assetSearchImportDTO.setEngine(assetSearchDTO.getEngine());
            assetSearchImportDTO.setProjectId(searchParam.getString("projectId"));
            assetSearchService.importAsset(assetSearchImportDTO, liteFlowTask.getId(), liteFlowSubTask.getId());
        } else {
            logService.addERRORLog("server", "获取资产失败!错误信息：" + result.getMessage(), liteFlowSubTask.getId());
        }
    }
}
