package org.jeecg.modules.testnet.server.service.client;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.client.ClientTools;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;

import java.util.List;

/**
 * @Description: 节点工具
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IClientToolsService extends IService<ClientTools> {

    void addConfig(Script script);

    void installTools(JSONArray ids);

    void changeStatus(String id, Boolean status);


    List<ClientTools> getAntoInstallTools(String clientId);
}
