package org.jeecg.modules.testnet.server.service.client;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.client.Client;

import java.util.List;

/**
 * @Description: 节点
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IClientService extends IService<Client> {

    Client getClientByName(String name);

    List<Client> getAllOnlineClients();

    void clearCache();

    long getOnlineClientsCount();

    long getOfflineClientsCount();

    void del(String ids);
}
