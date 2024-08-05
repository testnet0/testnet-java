package org.jeecg.modules.testnet.server.mapper.client;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.jeecg.modules.testnet.server.entity.client.ClientTools;

/**
 * @Description: 节点工具
 * @Author: jeecg-boot
 * @Date: 2024-07-24
 * @Version: V1.0
 */
public interface ClientToolsMapper extends BaseMapper<ClientTools> {

    @Delete("delete from client_tools where client_id = #{clientId}")
    void delToolsByClientId(String clientId);
}
