package org.jeecg.modules.testnet.server.listener;

import com.alibaba.fastjson.JSONArray;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;
import org.jeecg.modules.testnet.server.entity.client.ClientTools;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.client.IClientToolsService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.IScriptService;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import testnet.common.entity.liteflow.VersionMessage;
import testnet.common.utils.ObjectBase64Decoder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class VersionMessageListener implements StreamListener<String, ObjectRecord<String, VersionMessage>> {


    @Resource
    private IClientService clientService;

    @Resource
    private IChainService chainService;

    @Resource
    private IClientConfigService clientConfigService;

    @Resource
    private IScriptService scriptService;

    @Resource
    private IClientToolsService clientToolsService;


    @Override
    @SneakyThrows
    public void onMessage(ObjectRecord<String, VersionMessage> record) {
        VersionMessage versionMessage = null;
        try {
            versionMessage = ObjectBase64Decoder.decodeFields(record.getValue());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        String clientName = versionMessage.getClientName();
        String clientVersion = versionMessage.getClientVersion();
        Client client = clientService.getClientByName(clientName);
        if (client != null) {
            if (client.getStatus().equals("N")) {
                clientService.clearCache(clientName);
                log.info("开始自动安装工具:{}", clientName);
                // 自动安装工具
                List<ClientTools> clientToolsList = clientToolsService.getAntoInstallTools(client.getId());
                JSONArray idsJsonArray = new JSONArray();
                clientToolsList.forEach(tool -> idsJsonArray.add(tool.getId()));
                clientToolsService.installTools(idsJsonArray);
            }
            client.setStatus("Y");
            client.setClientVersion(clientVersion);
            clientService.updateById(client);
        } else {
            Client client1 = new Client();
            client1.setClientName(clientName);
            client1.setClientVersion(clientVersion);
            client1.setStatus("Y");
            clientService.save(client1);
            clientService.clearCache(clientName);
            addClientConfig(client1.getId());
            addClientTool(client1.getId());
            log.info("Client : {} insert success!", clientName);
        }
    }

    private void addClientConfig(String clientId) {
        List<Chain> chains = chainService.getAllChainList();
        List<ClientConfig> clientConfigs = new ArrayList<>();
        for (Chain chain : chains) {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setClientId(clientId);
            clientConfig.setChainId(chain.getId());
            clientConfig.setConfig(chain.getConfig());
            clientConfig.setConfigFile("N");
            clientConfig.setMaxThreads(chain.getDefaultThread());
            clientConfigs.add(clientConfig);
        }
        clientConfigService.saveBatch(clientConfigs);
    }

    private void addClientTool(String clientId) {
        List<Script> scriptList = scriptService.needInstallScript();
        List<ClientTools> clientToolsList = new ArrayList<>();
        for (Script script : scriptList) {
            ClientTools clientTools = new ClientTools();
            clientTools.setClientId(clientId);
            clientTools.setScriptId(script.getId());
            clientTools.setStatus(false);
            clientTools.setInstallCommand(script.getInstallCommand());
            clientTools.setUpdateCommand(script.getUpdateCommand());
            clientTools.setVersionCheckCommand(script.getVersionCheckCommand());
            clientToolsList.add(clientTools);
        }
        clientToolsService.saveBatch(clientToolsList);
    }
}