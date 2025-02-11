package org.jeecg.modules.testnet.server.service.liteflow;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 扫描任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface ILiteFlowTaskService extends IService<LiteFlowTask> {

    /**
     * 添加一对多
     *
     * @param liteFlowTask
     * @param liteFlowSubTaskList
     */
    public void saveMain(LiteFlowTask liteFlowTask, List<LiteFlowSubTask> liteFlowSubTaskList);

    /**
     * 修改一对多
     *
     * @param liteFlowTask
     * @param liteFlowSubTaskList
     */
    public void updateMain(LiteFlowTask liteFlowTask, List<LiteFlowSubTask> liteFlowSubTaskList);

    /**
     * 删除一对多
     *
     * @param id
     */
    public void delMain(String id);

    /**
     * 批量删除一对多
     *
     * @param idList
     */
    public void delBatchMain(Collection<? extends Serializable> idList);


    List<LiteFlowTask> getUndoList();

    LiteFlowTask getBySubTaskId(String id);


    <T extends AssetBase> void executeAgain(String id);

    Result<String> edit(LiteFlowTask liteFlowTask);

    void delJob(String id);

    Result<String> stopTask(String id);

    Result<String> changeCronStatus(String id, Boolean status);

    LiteFlowTask getByIdWithCache(String id);
}
