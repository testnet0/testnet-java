package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.vo.asset.AssetSubDomainVO;

import java.util.List;

/**
 * @Description: 子域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetSubDomainMapper extends BaseMapper<AssetSubDomain> {

    @Select("SELECT * FROM asset_sub_domain WHERE sub_domain = #{subdomain}")
    AssetSubDomain selectBySubdomain(String subdomain);

    @Select("SELECT COUNT(*) FROM asset_sub_domain WHERE domain_id = #{domainId}")
    long getSubDomainCountByDomain(String domainId);

    List<AssetSubDomainVO> getSubDomainListByIpId(String ipId);

    @Delete("DELETE FROM asset_sub_domain WHERE domain_id = #{domainId}")
    void deleteByDomainId(String domainId);

    @Select("SELECT id FROM asset_sub_domain WHERE domain_id = #{id}")
    List<String> getSubDomainIdsListByDomain(String id);
}
