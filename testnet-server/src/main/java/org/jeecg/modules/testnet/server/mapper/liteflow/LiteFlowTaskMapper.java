package org.jeecg.modules.testnet.server.mapper.liteflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;

import java.util.List;

/**
 * @Description: 扫描任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface LiteFlowTaskMapper extends BaseMapper<LiteFlowTask> {


    List<LiteFlowTask> getUndoList();

    LiteFlowTask getBySubTaskId(String subTaskId);

}
