package org.jeecg.modules.testnet.server.service.grpc;

import com.alibaba.fastjson.JSONObject;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.boot.starter.lock.client.RedissonLockClient;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;
import org.jeecg.modules.testnet.server.entity.client.ClientTools;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.IScriptService;
import org.jeecg.modules.testnet.server.service.log.ILogService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.yaml.snakeyaml.Yaml;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.enums.LiteFlowStatusEnums;
import testnet.grpc.ClientMessageProto.*;
import testnet.grpc.ClientMessageServiceGrpc;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@GrpcService
public class ClientMessageServiceImpl extends ClientMessageServiceGrpc.ClientMessageServiceImplBase {


    @Resource
    private RedissonLockClient redissonLockClient;

    @Resource
    private IClientService clientService;

    @Resource
    private ILogService logService;

    @Resource
    private IChainService chainService;

    @Resource
    private IClientConfigService clientConfigService;


    @Resource
    private IScriptService scriptService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;

    @Resource
    private Map<String, IAssetResultProcessorService> assetResultProcessMap;

    @Resource
    private ILiteFlowTaskService liteFlowTaskService;

    @Override
    public void reportClientStatus(ClientStatusMessage request, StreamObserver<ClientResponse> responseObserver) {
        String clientName = request.getClientName();
        String clientVersion = request.getClientVersion();
        if (redissonLockClient.tryLock(clientName, 10, 10)) {
            Client client = clientService.getClientByName(clientName);
            if (client != null) {
                // 如果当前是离线状态 清理缓存
                if (client.getStatus().equals("N")) {
                    clientService.clearCache();
                }
                client.setStatus("Y");
                client.setClientVersion(clientVersion);
                client.setCpuUsage(request.getCpuUsage());
                client.setFreeMemory((int) request.getFreeMemory());
                client.setTotalMemory((int) request.getTotalMemory());
                clientService.updateById(client);
                redissonLockClient.unlock(clientName);
            } else {
                client = new Client();
                client.setClientName(clientName);
                client.setClientVersion(clientVersion);
                client.setStatus("Y");
                clientService.save(client);
                clientService.clearCache();
                addClientConfig(client.getId());
                redissonLockClient.unlock(clientName);
                log.info("Client : {} insert success!", clientName);
            }
            ClientResponse response = ClientResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage(JSONObject.toJSONString(getPendingTask(client.getId())))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            ClientResponse response = ClientResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Client : " + clientName + " is processing!")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void reportLog(LogMessage request, StreamObserver<ClientResponse> responseObserver) {
        String taskId = request.getTaskId();
        String level = request.getLevel();
        String message = request.getMessage();
        if (taskId.isEmpty() || level.isEmpty()) {
            log.error("关键参数为空！pluginInstanceId: {}, level: {}", taskId, level);
            return;
        }
        switch (level) {
            case "INFO":
                log.info("实例ID：{}， 运行日志：{}", taskId, message);
                logService.addINFOLog(request.getClientName(), message, taskId);
                break;
            case "ERROR":
                logService.addERRORLog(request.getClientName(), message, taskId);
                log.error("实例ID：{}，错误日志：{}", taskId, message);
                break;
        }
        ClientResponse response = ClientResponse.newBuilder()
                .setSuccess(true)
                .setMessage("ok")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reportTaskStatus(TaskStatusMessage request, StreamObserver<ClientResponse> responseObserver) {
        String taskId = request.getTaskId();
        log.info("接收到任务ID:{},状态消息: {}", taskId, request.getTaskStatus());
        if (redissonLockClient.tryLock(taskId, 10, 10)) {
            LiteFlowSubTask liteFlowSubTask = liteFlowSubTaskService.getById(taskId);
            if (liteFlowSubTask != null) {
                if (liteFlowSubTask.getTaskStatus().equals(LiteFlowStatusEnums.RUNNING.name())) {
                    liteFlowSubTask.setTaskStatus(request.getTaskStatus());
                    liteFlowSubTaskService.updateById(liteFlowSubTask);
                } else {
                    log.info("任务已经完成，不能修改状态！");
                }
                redissonLockClient.unlock(taskId);
            }
        } else {
            log.error("任务状态获取锁失败！");
        }
        ClientResponse response = ClientResponse.newBuilder()
                .setSuccess(true)
                .setMessage("ok")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reportResult(ResultMessage request, StreamObserver<ClientResponse> responseObserver) {
        log.info("接收到结果消息: {}", request);
        String taskId = request.getTaskId();
        LiteFlowSubTask liteFlowSubTask = liteFlowSubTaskService.getById(taskId);
        if (liteFlowSubTask == null) {
            log.error("实例不存在: {}", taskId);
        } else {
            LiteFlowTask liteFlowTask = liteFlowTaskService.getByIdWithCache(liteFlowSubTask.getTaskId());
            Chain chain = chainService.getByIdWithCache(liteFlowTask.getChainId());
            if (chain != null && StringUtils.isNotBlank(chain.getProcessorClassName())) {
                IAssetResultProcessorService assetResultProcessService = assetResultProcessMap.get(chain.getProcessorClassName());
                if (assetResultProcessService != null) {
                    log.info("开始处理结果,类名:{}", chain.getProcessorClassName());
                    String assetId = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam()).getString("id");
                    assetResultProcessService.processAsset(assetId, chain.getChainName(), liteFlowTask, liteFlowSubTask, request);
                } else {
                    log.error("未找到处理类: {}", chain.getProcessorClassName());
                }
            }
        }
        ClientResponse response = ClientResponse.newBuilder()
                .setSuccess(true)
                .setMessage("ok")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void addClientConfig(String clientId) {
        List<Chain> chains = chainService.getAllChainList();
        List<ClientConfig> clientConfigs = new ArrayList<>();
        for (Chain chain : chains) {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setClientId(clientId);
            clientConfig.setChainId(chain.getId());
            clientConfig.setConfig(chain.getConfig());
            clientConfig.setMaxThreads(chain.getDefaultThread());
            clientConfigs.add(clientConfig);
        }
        clientConfigService.saveBatch(clientConfigs);
    }

    private List<TaskExecuteMessage> getPendingTask(String clientId) {
        List<TaskExecuteMessage> taskExecuteMessages = new ArrayList<>();
        List<LiteFlowTask> undoList = liteFlowTaskService.getUndoList();
        if (undoList != null && !undoList.isEmpty()) {
            log.info("扫描到{}条待执行任务", undoList.size());
            undoList.forEach(liteFlowTask -> {
                int currentThreadCount = liteFlowSubTaskService.getCurrentThreadCount(liteFlowTask.getChainId(), clientId);
                ClientConfig clientConfig = clientConfigService.getClientConfig(clientId, liteFlowTask.getChainId());
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
                            liteFlowSubTask.setClientId(clientId);
                            liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.RUNNING.name());
                            liteFlowSubTaskService.updateById(liteFlowSubTask);
                            TaskExecuteMessage taskExecuteMessage = getTaskExecuteMessage(liteFlowTask, liteFlowSubTask, chain);
                            taskExecuteMessages.add(taskExecuteMessage);
                            // sendMessage(liteFlowTask, liteFlowSubTask, chain, client.getClientName());
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
                return;
//                switch (liteFlowTask.getRouter()) {
//                    case "0":
//                        // 分配给空闲节点
//                        onlineClients.forEach(client -> {
//                            executeTask(liteFlowTask, client);
//                        });
//                        break;
//                    case "1":
//                        // 分配指定节点
//                        for (String clientId : liteFlowTask.getClientId().split(",")) {
//                            Client client = clientService.getById(clientId);
//                            if (client != null && client.getStatus().equals("Y")) {
//                                executeTask(liteFlowTask, client);
//                            }
//                        }
//                        break;
//                }
            });

        }
        return taskExecuteMessages;
    }

    private static TaskExecuteMessage getTaskExecuteMessage(LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, Chain chain) {
        TaskExecuteMessage taskExecuteMessage = new TaskExecuteMessage();
        taskExecuteMessage.setChainName(chain.getChainName());
        taskExecuteMessage.setTaskId(liteFlowSubTask.getId());
        taskExecuteMessage.setConfig(liteFlowSubTask.getConfig());
        taskExecuteMessage.setTaskParams(liteFlowSubTask.getSubTaskParam());
        taskExecuteMessage.setChainId(liteFlowTask.getChainId());
        taskExecuteMessage.setAssetType(liteFlowTask.getAssetType());
        return taskExecuteMessage;
    }
}

