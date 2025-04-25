package org.jeecg.modules.testnet.server.service.liteflow.impl;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.boot.starter.lock.client.RedissonLockClient;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.service.IQuartzJobService;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowSubTaskMapper;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.enums.LiteFlowStatusEnums;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @Description: 扫描任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class LiteFlowTaskServiceImpl extends ServiceImpl<LiteFlowTaskMapper, LiteFlowTask> implements ILiteFlowTaskService {

    @Autowired
    private LiteFlowTaskMapper liteFlowTaskMapper;
    @Autowired
    private LiteFlowSubTaskMapper liteFlowSubTaskMapper;

    @Resource
    private IAssetSearchService assetSearchService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private IQuartzJobService quartzJobService;

    @Resource
    private RedissonLockClient redissonLockClient;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(LiteFlowTask liteFlowTask, List<LiteFlowSubTask> liteFlowSubTaskList) {
        liteFlowTaskMapper.insert(liteFlowTask);
        if (liteFlowSubTaskList != null && liteFlowSubTaskList.size() > 0) {
            for (LiteFlowSubTask entity : liteFlowSubTaskList) {
                //外键设置
                entity.setTaskId(liteFlowTask.getId());
                liteFlowSubTaskMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMain(LiteFlowTask liteFlowTask, List<LiteFlowSubTask> liteFlowSubTaskList) {
        liteFlowTaskMapper.updateById(liteFlowTask);

        //1.先删除子表数据
        liteFlowSubTaskMapper.deleteByMainId(liteFlowTask.getId());

        //2.子表数据重新插入
        if (liteFlowSubTaskList != null && liteFlowSubTaskList.size() > 0) {
            for (LiteFlowSubTask entity : liteFlowSubTaskList) {
                //外键设置
                entity.setTaskId(liteFlowTask.getId());
                liteFlowSubTaskMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        liteFlowSubTaskMapper.deleteByMainId(id);
        liteFlowTaskMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            liteFlowSubTaskMapper.deleteByMainId(id.toString());
            liteFlowTaskMapper.deleteById(id);
            delJob((String) id);
        }
    }

    @Override
    public List<LiteFlowTask> getUndoList() {
        return liteFlowTaskMapper.getUndoList();
    }

    @Override
    public LiteFlowTask getBySubTaskId(String id) {
        return liteFlowTaskMapper.getBySubTaskId(id);
    }


    @Override
    @Async
    public void executeAgain(String id, Boolean failed) {
        LiteFlowTask liteFlowTask = liteFlowTaskMapper.selectById(id);
        if (liteFlowTask != null) {
            // 空间引擎导入需要单独处理
            if (liteFlowTask.getChainId().equals("1820100181514387457")) {
                assetSearchService.executeAgain(liteFlowTask);
            } else {
                Integer unFinishedChain = liteFlowTask.getUnFinishedChain();
                if (unFinishedChain > 0) {
                    log.error("请等上次任务全部执行完成后再执行！");
                    return;
                }
                Integer version = liteFlowTask.getVersion();
                if (version != null) {
                    liteFlowTask.setVersion(version + 1);
                    if (StringUtils.isNotBlank(liteFlowTask.getSearchParam())) {
                        // 说明是通过搜索创建
                        List<String> assetList = assetCommonOptionService.queryByAssetType(liteFlowTask.getSearchParam(), liteFlowTask.getAssetType());
                        if (assetList != null && !assetList.isEmpty()) {
                            liteFlowTask.setUnFinishedChain(assetList.size());
                            // 加锁 防止并发出现数量错误
                            if (redissonLockClient.tryLock(id, 10, 10)) {
                                liteFlowTaskMapper.updateById(liteFlowTask);
                            }
                            List<LiteFlowSubTask> subTaskList = new ArrayList<>();
                            assetList.forEach(asset -> {
                                LiteFlowSubTask subTask = new LiteFlowSubTask();
                                subTask.setTaskId(id);
                                subTask.setVersion(version);
                                subTask.setTaskStatus(LiteFlowStatusEnums.PENDING.name());
                                subTask.setSubTaskParam(asset);
                                subTaskList.add(subTask);
                            });
                            liteFlowSubTaskService.saveBatch(subTaskList);
                        }
                    } else {
                        LambdaQueryWrapper<LiteFlowSubTask> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(LiteFlowSubTask::getTaskId, id);
                        queryWrapper.eq(LiteFlowSubTask::getVersion, version);
                        if (failed) {
                            queryWrapper.eq(LiteFlowSubTask::getTaskStatus, LiteFlowStatusEnums.FAILED.name());
                        }
                        List<LiteFlowSubTask> liteFlowSubTasks = liteFlowSubTaskMapper.selectList(queryWrapper);
                        liteFlowTask.setUnFinishedChain(liteFlowSubTasks.size());
                        // 加锁 防止并发出现数量错误
                        if (!liteFlowSubTasks.isEmpty()) {
                            if (redissonLockClient.tryLock(id, 10, 10)) {
                                liteFlowTaskMapper.updateById(liteFlowTask);
                            }
                        }
                        List<LiteFlowSubTask> newLiteFlowSubTasks = new ArrayList<>();
                        liteFlowSubTasks.forEach(liteFlowSubTask -> {
                            LiteFlowSubTask newLiteFlowSubTask = new LiteFlowSubTask();
                            newLiteFlowSubTask.setVersion(version + 1);
                            newLiteFlowSubTask.setTaskId(id);
                            newLiteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.PENDING.name());
                            if (StringUtils.isNotBlank(liteFlowTask.getAssetType())) {
                                JSONObject jsonObject = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam());
                                Map<String, String> map = new HashMap<>();
                                map.put("id", jsonObject.getString("id"));
                                Result<? extends AssetBase> asset = assetCommonOptionService.getDTOByFieldAndAssetType(map, AssetTypeEnums.fromCode(liteFlowTask.getAssetType()));
                                if (asset != null) {
                                    newLiteFlowSubTask.setSubTaskParam(JSONObject.toJSONString(asset.getResult()));
                                    newLiteFlowSubTasks.add(newLiteFlowSubTask);
                                }
                            } else {
                                newLiteFlowSubTask.setSubTaskParam(liteFlowSubTask.getSubTaskParam());
                                newLiteFlowSubTasks.add(newLiteFlowSubTask);
                            }
                        });
                        liteFlowSubTaskService.saveBatch(newLiteFlowSubTasks);
                    }
                } else {
                    log.error("未找到对应的任务：{}", id);
                }
            }
        }
    }

    @Override
    public Result<String> edit(LiteFlowTask liteFlowTask) {
        if (liteFlowTask.getIsCron() == 1) {
            if (StringUtils.isEmpty(liteFlowTask.getJobCron())) {
                return Result.error("定时任务必须设置cron表达式");
            } else {
                if (StringUtils.isEmpty(liteFlowTask.getQuartzJobId())) {
                    QuartzJob quartzJob = new QuartzJob();
                    quartzJob.setStatus(CommonConstant.STATUS_NORMAL);
                    quartzJob.setJobClassName("org.jeecg.modules.testnet.server.job.RunChainJob");
                    quartzJob.setCronExpression(liteFlowTask.getJobCron());
                    quartzJob.setParameter(liteFlowTask.getId());
                    quartzJobService.saveAndScheduleJob(quartzJob);
                    liteFlowTask.setQuartzJobId(quartzJob.getId());
                } else {
                    QuartzJob quartzJob = quartzJobService.getById(liteFlowTask.getQuartzJobId());
                    quartzJob.setCronExpression(liteFlowTask.getJobCron());
                    try {
                        quartzJobService.editAndScheduleJob(quartzJob);
                    } catch (Exception e) {
                        log.error("定时任务更新失败：{}", e.getMessage());
                    }
                }
            }
        } else {
            if (StringUtils.isNotEmpty(liteFlowTask.getQuartzJobId())) {
                QuartzJob quartzJob = quartzJobService.getById(liteFlowTask.getQuartzJobId());
                quartzJobService.pause(quartzJob);
            }
        }
        updateById(liteFlowTask);
        return Result.ok("更新成功！");
    }

    @Override
    public void delJob(String id) {
        LiteFlowTask liteFlowTask = getById(id);
        if (liteFlowTask != null && StringUtils.isNotBlank(liteFlowTask.getQuartzJobId())) {
            QuartzJob quartzJob = quartzJobService.getById(liteFlowTask.getQuartzJobId());
            if (quartzJob != null) {
                quartzJobService.deleteAndStopJob(quartzJob);
            }
        }
    }

    @Override
    public Result<String> stopTask(String id) {
        List<LiteFlowSubTask> liteFlowSubTasks = liteFlowSubTaskService.getPendingList(id, 0);
        if (liteFlowSubTasks != null && !liteFlowSubTasks.isEmpty()) {
            List<LiteFlowSubTask> liteFlowSubTaskList = new ArrayList<>();
            liteFlowSubTasks.forEach(liteFlowSubTask -> {
                liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.CANCELED.name());
                liteFlowSubTaskList.add(liteFlowSubTask);
            });
            liteFlowSubTaskService.updateBatchById(liteFlowSubTaskList);
            LiteFlowTask liteFlowTask = getById(id);
            liteFlowTask.setUnFinishedChain(0);
            if (redissonLockClient.tryLock(id, 10, 10)) {
                liteFlowTaskMapper.updateById(liteFlowTask);
            }
            redissonLockClient.unlock(liteFlowTask.getId());
            return Result.ok("停止成功！");
        } else {
            return Result.error("没有准备中的任务！");
        }
    }

    @Override
    public Result<String> changeCronStatus(String id, Boolean status) {
        LiteFlowTask liteFlowTask = getById(id);
        if (status) {
            if (StringUtils.isEmpty(liteFlowTask.getJobCron())) {
                return Result.error("定时任务必须设置cron表达式");
            } else {
                QuartzJob quartzJob = quartzJobService.getById(liteFlowTask.getQuartzJobId());
                quartzJobService.resumeJob(quartzJob);
                liteFlowTask.setIsCron(1);
                updateById(liteFlowTask);
                return Result.ok("定时任务启动成功！");
            }
        } else {
            QuartzJob quartzJob = quartzJobService.getById(liteFlowTask.getQuartzJobId());
            quartzJobService.pause(quartzJob);
            liteFlowTask.setIsCron(0);
            updateById(liteFlowTask);
            return Result.ok("定时任务停止成功！");
        }
    }

    @Override
    @Cacheable(value = "liteflow:task:cache", key = "#id", unless = "#result == null ")
    public LiteFlowTask getByIdWithCache(String id) {
        return getById(id);
    }

}
