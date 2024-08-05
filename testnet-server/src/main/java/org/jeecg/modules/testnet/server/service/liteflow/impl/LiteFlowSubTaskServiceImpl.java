package org.jeecg.modules.testnet.server.service.liteflow.impl;


import cn.hutool.core.codec.Base64Decoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowSubTaskMapper;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskMapper;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.vo.LiteflowInstanceLogVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    private JeecgElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private LiteFlowSubTaskMapper liteFlowSubTaskMapper;

    @Resource
    private LiteFlowTaskMapper liteFlowTaskMapper;

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
    public IPage<LiteflowInstanceLogVO> getLogById(String id, Integer pageNo, Integer pageSize) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskId", id);
        JSONObject match = new JSONObject();
        match.put("match", jsonObject);
        JSONObject finalJson = elasticsearchTemplate.buildQuery(null, match, (long) (pageNo - 1) * pageSize, pageSize);
        JSONObject result = elasticsearchTemplate.search(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, finalJson);
        List<LiteflowInstanceLogVO> liteflowInstanceLogVOList = new ArrayList<>();
        IPage<LiteflowInstanceLogVO> page = new Page<>();
        if (result != null && result.getJSONObject("hits") != null) {
            JSONObject hits = result.getJSONObject("hits");
            JSONArray jsonArray = hits.getJSONArray("hits");
            page.setTotal(hits.getJSONObject("total").getInteger("value"));
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i).getJSONObject("_source");
                LiteflowInstanceLogVO liteflowInstanceLogVO = JSON.parseObject(json.toJSONString(), LiteflowInstanceLogVO.class);
                liteflowInstanceLogVO.setMessage(Base64Decoder.decodeStr(liteflowInstanceLogVO.getMessage()));
                liteflowInstanceLogVOList.add(liteflowInstanceLogVO);
            }
        }
        liteflowInstanceLogVOList.sort((o1, o2) -> {
            long timestamp1 = Long.parseLong(o1.getTimestamp());
            long timestamp2 = Long.parseLong(o2.getTimestamp());
            return Long.compare(timestamp1, timestamp2);
        });
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        page.setRecords(liteflowInstanceLogVOList);
        return page;
    }

    @Override
    public void cancelSubTask(String ids) {
        Map<String, Integer> map = new HashMap<>();
        for (String id : ids.split(",")) {
            LiteFlowSubTask liteFlowSubTask = getById(id);
            if (liteFlowSubTask != null && (liteFlowSubTask.getTaskStatus().equals(LiteFlowStatusEnums.PENDING.name()) || liteFlowSubTask.getTaskStatus().equals(LiteFlowStatusEnums.RUNNING.name()))) {
                liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.CANCELED.name());
                updateById(liteFlowSubTask);
                if (!map.containsKey(liteFlowSubTask.getTaskId())) {
                    map.put(liteFlowSubTask.getTaskId(), 1);
                }
            }
            map.forEach((k, v) -> {
                // 更新主表未完成任务数量 并发的时候可能会导致数量不准确，但是不影响使用
                LiteFlowTask liteFlowTask = liteFlowTaskMapper.selectById(k);
                liteFlowTask.setUnFinishedChain(liteFlowSubTaskMapper.getUndoCountByTaskId(k));
                liteFlowTaskMapper.updateById(liteFlowTask);
            });
        }
    }


    @Override
    public int getCurrentThreadCount(String chainId, String clientId) {
        return liteFlowSubTaskMapper.getCurrentThreadCount(chainId, clientId);
    }

}
