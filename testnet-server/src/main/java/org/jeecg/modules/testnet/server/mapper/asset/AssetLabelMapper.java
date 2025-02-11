package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.AssetLabel;

/**
 * @Description: 资产标签
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetLabelMapper extends BaseMapper<AssetLabel> {

    /**
     * 根据标签id或标签名查询标签
     *
     * @param s
     * @return AssetLabel
     */
    @Select("select * from asset_label where id = #{s} or label_name = #{s}")
    AssetLabel getByLabelIdOrName(String s);
}
