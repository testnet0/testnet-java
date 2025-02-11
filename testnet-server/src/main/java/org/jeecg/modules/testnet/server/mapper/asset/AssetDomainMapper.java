package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;

/**
 * @Description: 主域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetDomainMapper extends BaseMapper<AssetDomain> {

    boolean updateDnsServer(String domainId, String dnsServer);

    boolean updateDomainWhois(String domainId, String whois);

    void updateCompanyAndIcpNumber(String domainId, String companyId, String icpNumber);

    @Update("update asset_domain set company_id = null where company_id = #{companyId}")
    void removeCompanyRelation(String companyId);
}
