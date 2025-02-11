package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;

/**
 * @Description: 公司
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetCompanyMapper extends BaseMapper<AssetCompany> {

    @Select("select * from asset_company where id=#{s} or company_name=#{s}")
    AssetCompany getCompanyByIdORName(String s);
}
