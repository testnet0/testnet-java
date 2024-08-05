package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.testnet.server.entity.asset.AssetApi;

import java.util.List;
import java.util.Map;

/**
 * @Description: API
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetApiMapper extends BaseMapper<AssetApi> {

    /**
     * 编辑节点状态
     *
     * @param id
     * @param status
     */
    void updateTreeNodeStatus(@Param("id") String id, @Param("status") String status);

    /**
     * 【vue3专用】根据父级ID查询树节点数据
     *
     * @param pid
     * @param query
     * @return
     */
    List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

    long getCountByProject(String id);

    void delByProjectId(String projectId);

    void delByAssetWebTreeId(String id);
}
