package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.jeecg.modules.testnet.server.entity.asset.AssetVul;

/**
 * @Description: 漏洞
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetVulMapper extends BaseMapper<AssetVul> {
    long getVulCountByProject(String id);

    @Delete("DELETE FROM asset_vul WHERE asset_id = #{subDomainId} AND asset_type = 'sub_domain'")
    void delBySubDomainId(String subDomainId);

    @Delete("DELETE FROM asset_vul WHERE asset_id = #{ipId} AND asset_type = 'ip'")
    void delByIpId(String ipId);

    @Delete("DELETE FROM asset_vul WHERE asset_id = #{webId} AND asset_type = 'web'")
    void delByWebId(String webId);
}
