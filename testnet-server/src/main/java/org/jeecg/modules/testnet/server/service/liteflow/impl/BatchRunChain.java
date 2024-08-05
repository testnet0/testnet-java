/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.liteflow.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import testnet.common.enums.LiteFlowStatusEnums;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BatchRunChain {


    @Resource
    private IAssetCommonOptionService assetCommonSearchService;
    @Resource
    private ILiteFlowTaskService liteFlowTaskService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;

    @Resource
    private IChainService chainService;


    @Async
    public void batchRun(String params, Integer version) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        if (jsonObject != null) {
            String assetType = jsonObject.getString("assetType");
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            LiteFlowTask task = new LiteFlowTask();
            task.setAssetType(assetType);
            String chainId = jsonObject.getString("chainId");
            task.setVersion(version);
            task.setChainId(chainId);
            task.setRouter("0");
            Chain chain = chainService.getById(chainId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime now = LocalDateTime.now(); // 获取当前日期时间
            String currentDateTime = now.format(formatter); // 格式化当前日期时间
            task.setTaskName(chain.getChainName() + "-任务-" + currentDateTime);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                task.setAssetNum(jsonArray.size());
                task.setUnFinishedChain(jsonArray.size());
                liteFlowTaskService.saveMain(task, null);
                // 批量扫描任务
                jsonArray.forEach(o -> {
                    // 创建子任务
                    LiteFlowSubTask subTask = new LiteFlowSubTask();
                    JSONObject jsonObject1 = (JSONObject) o;
                    subTask.setTaskId(task.getId());
                    subTask.setVersion(version);
                    subTask.setTaskStatus(LiteFlowStatusEnums.PENDING.name());
                    subTask.setSubTaskParam(jsonObject1.toJSONString());
                    liteFlowSubTaskService.save(subTask);
                });
            } else {
                List<String> assetList = assetCommonSearchService.queryByAssetType(params, assetType);
                task.setUnFinishedChain(assetList.size());
                task.setSearchParam(jsonObject.toJSONString());
                liteFlowTaskService.saveMain(task, null);
                if (!assetList.isEmpty()) {
                    List<LiteFlowSubTask> subTaskList = new ArrayList<>();
                    assetList.forEach(asset -> {
                        LiteFlowSubTask subTask = new LiteFlowSubTask();
                        subTask.setTaskId(task.getId());
                        subTask.setVersion(version);
                        subTask.setTaskStatus(LiteFlowStatusEnums.PENDING.name());
                        subTask.setSubTaskParam(asset);
                        subTaskList.add(subTask);
                    });
                    liteFlowSubTaskService.saveBatch(subTaskList);
                }
            }

        }
    }
}
