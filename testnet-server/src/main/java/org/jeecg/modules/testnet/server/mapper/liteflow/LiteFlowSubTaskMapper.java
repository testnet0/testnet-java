package org.jeecg.modules.testnet.server.mapper.liteflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;

import java.util.List;

/**
 * @Description: 子任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface LiteFlowSubTaskMapper extends BaseMapper<LiteFlowSubTask> {

    /**
     * 通过主表id删除子表数据
     *
     * @param mainId 主表id
     * @return boolean
     */
    public boolean deleteByMainId(@Param("mainId") String mainId);

    /**
     * 通过主表id查询子表数据
     *
     * @param mainId 主表id
     * @return List<LiteFlowSubTask>
     */
    public List<LiteFlowSubTask> selectByMainId(@Param("mainId") String mainId);

    List<LiteFlowSubTask> getPendingList(String mainId, int size);

    int getCurrentThreadCount(String chainId, String clientId);

    List<LiteFlowSubTask> getRunningList();

    int getUndoCountByTaskId(String k);
}
