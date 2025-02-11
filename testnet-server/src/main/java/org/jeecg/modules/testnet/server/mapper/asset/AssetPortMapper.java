package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;

import java.util.List;

/**
 * @Description: 端口
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetPortMapper extends BaseMapper<AssetPort> {

    List<AssetPort> listByDomainId(String domainId);

    long getCountByProject(String id);

    void delByProjectId(String projectId);

    long getPortsCountByIpId(String ipId);

    List<AssetPort> getPortsByIpId(String id);

    @Select("SELECT id FROM asset_port WHERE ip = #{id}")
    List<String> getPortIdsByIpId(String id);

    @Delete("DELETE FROM asset_port WHERE ip = #{id}")
    void delByIpId(String id);
}
