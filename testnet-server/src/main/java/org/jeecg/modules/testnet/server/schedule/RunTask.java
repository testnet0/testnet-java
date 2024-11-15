/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-06-25
 **/
package org.jeecg.modules.testnet.server.schedule;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.boot.starter.lock.client.RedissonLockClient;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.enums.LiteFlowStatusEnums;
import testnet.common.service.IRedisStreamService;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class RunTask {

    @Resource
    private ILiteFlowTaskService liteFlowTaskService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;

    @Resource
    private IChainService chainService;


    @Resource
    private IClientConfigService clientConfigService;

    @Resource
    private IClientService clientService;


    @Resource
    private IRedisStreamService redisStreamService;

    @Resource
    private RedissonLockClient redissonLockClient;


    @Scheduled(fixedRate = 5 * 1000L) // 每5秒刷新一次
    public void scanTask() {
        List<LiteFlowTask> undoList = liteFlowTaskService.getUndoList();
        if (undoList != null && !undoList.isEmpty()) {
            List<Client> onlineClients = clientService.getAllOnlineClients();
            if (onlineClients == null || onlineClients.isEmpty()) {
                log.error("没有在线客户端可以执行任务！");
                return;
            }
            log.info("扫描到{}条待执行任务", undoList.size());
            undoList.forEach(liteFlowTask -> {
                switch (liteFlowTask.getRouter()) {
                    case "0":
                        // 分配给空闲节点
                        onlineClients.forEach(client -> {
                            executeTask(liteFlowTask, client);
                        });
                        break;
                    case "1":
                        // 分配指定节点
                        for (String clientId : liteFlowTask.getClientId().split(",")) {
                            Client client = clientService.getById(clientId);
                            if (client != null && client.getStatus().equals("Y")) {
                                executeTask(liteFlowTask, client);
                            }
                        }
                        break;
                }
            });

        }

    }

    private void executeTask(LiteFlowTask liteFlowTask, Client client) {
        int currentThreadCount = liteFlowSubTaskService.getCurrentThreadCount(liteFlowTask.getChainId(), client.getId());
        ClientConfig clientConfig = clientConfigService.getClientConfig(client.getId(), liteFlowTask.getChainId());
        Integer maxThreads = 0;
        String config;
        Chain chain = chainService.getById(liteFlowTask.getChainId());
        if (clientConfig == null) {
            log.info("任务:{} 没有配置，使用默认配置", liteFlowTask.getChainId());
            if (chain == null) {
                log.error("任务:{} 没有默认配置,执行失败！", liteFlowTask.getChainId());
                return;
            } else {
                maxThreads = chain.getDefaultThread();
                config = chain.getConfig();
            }
        } else {
            config = clientConfig.getConfig();
            maxThreads = clientConfig.getMaxThreads();
        }
        if (currentThreadCount < maxThreads) {
            List<LiteFlowSubTask> subTaskList = liteFlowSubTaskService.getPendingList(liteFlowTask.getId(), (maxThreads - currentThreadCount));
            if (subTaskList != null && !subTaskList.isEmpty()) {
                subTaskList.forEach(liteFlowSubTask -> {
                    if (StringUtils.isNotBlank(config)) {
                        Yaml yaml = new Yaml();
                        liteFlowSubTask.setConfig(JSONObject.toJSONString(yaml.load(config)));
                    }
                    liteFlowSubTask.setClientId(client.getId());
                    liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.RUNNING.name());
                    liteFlowSubTaskService.updateById(liteFlowSubTask);
                    sendMessage(liteFlowTask, liteFlowSubTask, chain, client.getClientName());
                });
                liteFlowTask.setUnFinishedChain(liteFlowTask.getUnFinishedChain() - subTaskList.size());
                if (redissonLockClient.tryLock(liteFlowTask.getId(), 10, 10)) {
                    try {
                        liteFlowTaskService.updateById(liteFlowTask);
                    } catch (Exception e) {
                        log.error("更新主表未完成任务数量失败!", e);
                    } finally {
                        redissonLockClient.unlock(liteFlowTask.getId());
                    }
                } else {
                    log.error("获取锁失败，更新主表未完成任务数量失败!");
                }
            }
        }
    }

    private void sendMessage(LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, Chain chain, String clientName) {
        TaskExecuteMessage taskExecuteMessage = new TaskExecuteMessage();
        taskExecuteMessage.setChainName(chain.getChainName());
        taskExecuteMessage.setTaskId(liteFlowSubTask.getId());
        taskExecuteMessage.setConfig(liteFlowSubTask.getConfig());
        taskExecuteMessage.setTaskParams(liteFlowSubTask.getSubTaskParam());
        taskExecuteMessage.setChainId(liteFlowTask.getChainId());
        taskExecuteMessage.setAssetType(liteFlowTask.getAssetType());
        String recordId = redisStreamService.addObject(Constants.STREAM_KEY_TASK_EXECUTE + clientName, taskExecuteMessage);
        log.info("发送消息成功，消息ID:{}", recordId);
    }
}
