package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.testnet.server.entity.asset.AssetApiTree;

import java.util.List;
import java.util.Map;

/**
 * @Description: API
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetApiTreeMapper extends BaseMapper<AssetApiTree> {


    List<SelectTreeModel> queryListByPid(@Param("pid") String pid, @Param("query") Map<String, String> query);

    List<AssetApiTree> selectIdAndPidList(List<String> pidList);
}
