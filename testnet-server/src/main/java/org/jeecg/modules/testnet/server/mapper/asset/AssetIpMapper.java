package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.vo.asset.AssetIpVO;

import java.util.List;

/**
 * @Description: ip
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetIpMapper extends BaseMapper<AssetIp> {

    @Select("SELECT * FROM asset_ip WHERE ip = #{ip}")
    AssetIp selectByIp(String ip);

    long getPortCountBySubDomainId(String domainId);

    long getPortCountByIpId(String ipId);

    List<AssetIpVO> getBySubDomainId(String id);
}