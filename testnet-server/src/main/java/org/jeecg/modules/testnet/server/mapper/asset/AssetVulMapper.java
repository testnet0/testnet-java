package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.testnet.server.entity.asset.AssetVul;

import java.util.List;

/**
 * @Description: 漏洞
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetVulMapper extends BaseMapper<AssetVul> {
    long getVulCountByProject(String id);

    void delBySubDomainIds(List<String> subDomainId);

    void delByIpIds(List<String> ipId);

    void delByWebIds(List<String> webId);
}
