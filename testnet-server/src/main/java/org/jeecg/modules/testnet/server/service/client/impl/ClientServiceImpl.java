package org.jeecg.modules.testnet.server.service.client.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.mapper.client.ClientConfigMapper;
import org.jeecg.modules.testnet.server.mapper.client.ClientMapper;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 节点
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements IClientService {


    @Resource
    private ClientConfigMapper clientConfigMapper;

    @Override
    @Cacheable(value = "workflow:client:cache#600", key = "#name", unless = "#result == null ")
    public Client getClientByName(String name) {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getClientName, name);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public List<Client> getAllOnlineClients() {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getStatus, "Y");
        return list(lambdaQueryWrapper);
    }

    @Override
    @CacheEvict(value = "workflow:client:cache#600", allEntries = true)
    public void clearCache() {

    }

    @Override
    public long getOnlineClientsCount() {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getStatus, "Y");
        return count(lambdaQueryWrapper);
    }

    @Override
    public long getOfflineClientsCount() {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getStatus, "N");
        return count(lambdaQueryWrapper);
    }

    @Override
    public void del(String ids) {
        for (String id : ids.split(",")) {
            clientConfigMapper.delByClientId(id);
            removeById(id);
            clearCache();
        }
    }
}
