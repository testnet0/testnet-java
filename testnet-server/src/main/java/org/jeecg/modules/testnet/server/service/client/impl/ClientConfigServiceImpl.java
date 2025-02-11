package org.jeecg.modules.testnet.server.service.client.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.mapper.client.ClientConfigMapper;
import org.jeecg.modules.testnet.server.mapper.client.ClientMapper;
import org.jeecg.modules.testnet.server.mapper.liteflow.ChainMapper;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 节点配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class ClientConfigServiceImpl extends ServiceImpl<ClientConfigMapper, ClientConfig> implements IClientConfigService {

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ChainMapper chainMapper;

    @Override
    public ClientConfig getClientConfig(String clientId, String chainId) {
        LambdaQueryWrapper<ClientConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientConfig::getClientId, clientId);
        queryWrapper.eq(ClientConfig::getChainId, chainId);
        return getOne(queryWrapper);
    }

    @Override
    public void delByChainIds(List<String> chainId) {
        chainId.forEach(id -> {
            LambdaQueryWrapper<ClientConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClientConfig::getChainId, id);
            remove(queryWrapper);
        });
    }

    @Override
    public void addConfig(Chain chain) {
        List<Client> clientList = clientMapper.selectList(null);
        clientList.forEach(client -> {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setMaxThreads(4);
            clientConfig.setClientId(client.getId());
            clientConfig.setChainId(chain.getId());
            clientConfig.setConfig(chain.getConfig());
            save(clientConfig);
        });
    }
}
