package org.jeecg.modules.testnet.server.service.liteflow.impl;


import cn.hutool.core.codec.Base64Decoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.lucene.store.Directory;
import org.jeecg.boot.starter.lock.client.RedissonLockClient;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowSubTaskMapper;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskMapper;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.lucene.LuceneService;
import org.jeecg.modules.testnet.server.vo.LiteflowInstanceLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import testnet.common.constan.Constants;
import testnet.common.enums.LiteFlowStatusEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 子任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class LiteFlowSubTaskServiceImpl extends ServiceImpl<LiteFlowSubTaskMapper, LiteFlowSubTask> implements ILiteFlowSubTaskService {

    @Resource
    private LuceneService luceneService;

    @Autowired
    private LiteFlowSubTaskMapper liteFlowSubTaskMapper;

    @Resource
    private LiteFlowTaskMapper liteFlowTaskMapper;

    @Resource
    private RedissonLockClient redissonLockClient;

    @Autowired
    @Qualifier("logDirectory")
    private Directory logDirectory;

    @Override
    public List<LiteFlowSubTask> selectByMainId(String mainId) {
        return liteFlowSubTaskMapper.selectByMainId(mainId);
    }

    @Override
    public IPage<LiteFlowSubTask> selectByMainId(String mainId, Integer pageNo, Integer pageSize) {
        IPage<LiteFlowSubTask> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<LiteFlowSubTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiteFlowSubTask::getTaskId, mainId);
        queryWrapper.orderByDesc(LiteFlowSubTask::getId);
        return page(page, queryWrapper);
    }

    @Override
    public List<LiteFlowSubTask> getPendingList(String mainId, int size) {
        return liteFlowSubTaskMapper.getPendingList(mainId, size);
    }

    @Override
    public List<LiteFlowSubTask> getRunningList() {
        return liteFlowSubTaskMapper.getRunningList();
    }


    @Override
    public long getCountByStatus(String name) {
        LambdaQueryWrapper<LiteFlowSubTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiteFlowSubTask::getTaskStatus, name);
        return count(queryWrapper);
    }

    @Override
    public long getCount() {
        return count();
    }

    @Override
    public IPage<JSONObject> getLogById(String id, Integer pageNo, Integer pageSize) {
        return luceneService.searchLogsByTaskId(id, pageNo, pageSize);
    }

    @Override
    public void cancelSubTask(String ids) {
        Map<String, Integer> map = new HashMap<>();
        List<LiteFlowSubTask> liteFlowSubTaskList = new ArrayList<>();
        for (String id : ids.split(",")) {
            LiteFlowSubTask liteFlowSubTask = getById(id);
            if (liteFlowSubTask != null && (liteFlowSubTask.getTaskStatus().equals(LiteFlowStatusEnums.PENDING.name()) || liteFlowSubTask.getTaskStatus().equals(LiteFlowStatusEnums.RUNNING.name()))) {
                liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.CANCELED.name());
                liteFlowSubTaskList.add(liteFlowSubTask);
                if (!map.containsKey(liteFlowSubTask.getTaskId())) {
                    map.put(liteFlowSubTask.getTaskId(), 1);
                }
            }
        }
        updateBatchById(liteFlowSubTaskList);
        map.forEach((k, v) -> {
            // 更新主表未完成任务数量
            if (redissonLockClient.tryLock(k, 10, 10)) {
                LiteFlowTask liteFlowTask = liteFlowTaskMapper.selectById(k);
                liteFlowTask.setUnFinishedChain(liteFlowSubTaskMapper.getUndoCountByTaskId(k));
                liteFlowTaskMapper.updateById(liteFlowTask);
                redissonLockClient.unlock(k);
            } else {
                log.error("获取锁失败，更新主表未完成任务数量失败");
            }
        });
    }


    @Override
    public int getCurrentThreadCount(String chainId, String clientId) {
        return liteFlowSubTaskMapper.getCurrentThreadCount(chainId, clientId);
    }

}
