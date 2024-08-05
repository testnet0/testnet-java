package org.jeecg.modules.testnet.server.mapper.client;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;

/**
 * @Description: 节点配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface ClientConfigMapper extends BaseMapper<ClientConfig> {

    @Delete("delete from client_config where client_id = #{clientId}")
    void delByClientId(String id);

}
