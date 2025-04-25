package org.jeecg.modules.testnet.server.service.liteflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;

import java.util.List;

/**
 * @Description: 子任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface ILiteFlowSubTaskService extends IService<LiteFlowSubTask> {

    /**
     * 通过主表id查询子表数据
     *
     * @param mainId 主表id
     * @return List<LiteFlowSubTask>
     */
    public List<LiteFlowSubTask> selectByMainId(String mainId);

    IPage<LiteFlowSubTask> selectByMainId(String mainId, Integer pageNo, Integer pageSize);

    List<LiteFlowSubTask> getPendingList(String mainId, int size);

    List<LiteFlowSubTask> getRunningList();

    long getCountByStatus(String name);

    long getCount();

    IPage<JSONObject> getLogById(String id, Integer pageNo, Integer pageSize,String keyword);

    void cancelSubTask(String id);

    int getCurrentThreadCount(String chainId, String clientId);

}
