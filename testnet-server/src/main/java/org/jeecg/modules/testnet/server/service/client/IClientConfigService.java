package org.jeecg.modules.testnet.server.service.client;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;

import java.util.List;

/**
 * @Description: 节点配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IClientConfigService extends IService<ClientConfig> {

    ClientConfig getClientConfig(String clientId, String chainId);

    void delByChainIds(List<String> list);

    void addConfig(Chain chain);

    List<ClientConfig> getClientFile(String clientId);

}
