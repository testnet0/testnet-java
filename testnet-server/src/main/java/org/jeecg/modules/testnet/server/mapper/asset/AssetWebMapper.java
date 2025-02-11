package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;

import java.util.List;

/**
 * @Description: WEB服务
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface AssetWebMapper extends BaseMapper<AssetWeb> {

    long getCountByProject(String id);

    void delByPort(String portId);

    void delByProjectId(String projectId);

    List<String> findWebByPortId(String id);


    List<AssetWeb> getWebBySubDomainId(String subDomainId);


    List<String> getWebIdBySubDomainId(String subDomainId);


    void delRelation(List<String> assetWebList);


    void deleteBySubDomainId(String subDomainId);


    void deleteByPortId(List<String> portId);

    void deleteBySubDomainIds(List<String> list);
}
