package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
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

    @Select("SELECT * FROM asset_sub_domain WHERE sub_domain = #{subdomain} AND project_id = #{projectId}")
    AssetSubDomain selectBySubdomain(@Param("subdomain") String subdomain, @Param("projectId") String projectId);

    @Select("SELECT COUNT(*) FROM asset_sub_domain WHERE domain_id = #{domainId}")
    long getSubDomainCountByDomain(String domainId);

    List<AssetSubDomainVO> getSubDomainListByIpId(String ipId);

    @Delete("DELETE FROM asset_sub_domain WHERE domain_id = #{domainId}")
    void deleteByDomainId(String domainId);

    @Select("SELECT id FROM asset_sub_domain WHERE domain_id = #{id}")
    List<String> getSubDomainIdsListByDomain(String id);

    IPage<AssetSubDomain> selectPageWithRelation(IPage<AssetSubDomain> page, @Param("ipId") String ipId);

}
