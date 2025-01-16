package org.jeecg.modules.testnet.server.service.client.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.client.ClientTools;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;
import org.jeecg.modules.testnet.server.mapper.client.ClientToolsMapper;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.client.IClientToolsService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.springframework.stereotype.Service;
import testnet.common.enums.LiteFlowStatusEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 节点工具
 * @Author: jeecg-boot
 * @Date: 2024-07-24
 * @Version: V1.0
 */
@Service
public class ClientToolsServiceImpl extends ServiceImpl<ClientToolsMapper, ClientTools> implements IClientToolsService {

    @Resource
    private IClientService clientService;

    @Resource
    private ILiteFlowTaskService liteFlowTaskService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;


    @Override
    public void addConfig(Script script) {
        List<ClientTools> clientTools = new ArrayList<>();
        clientService.list().forEach(client -> {
            ClientTools clientTool = new ClientTools();
            clientTool.setClientId(client.getId());
            clientTool.setScriptId(script.getId());
            clientTool.setInstallCommand(script.getInstallCommand());
            clientTool.setVersionCheckCommand(script.getVersionCheckCommand());
            clientTool.setStatus(false);
            clientTools.add(clientTool);
        });
        saveBatch(clientTools);
    }

    @Override
    public void installTools(JSONArray ids) {
        Map<String, List<ClientTools>> clientToolsMap = new HashMap<>();
        ids.forEach(id -> {
            ClientTools clientTools = getById((String) id);
            if (clientTools != null) {
                if (!clientToolsMap.containsKey(clientTools.getClientId())) {
                    List<ClientTools> clientToolsList = new ArrayList<>();
                    clientToolsList.add(clientTools);
                    clientToolsMap.put(clientTools.getClientId(), clientToolsList);
                } else {
                    List<ClientTools> oldToolsList = clientToolsMap.get(clientTools.getClientId());
                    oldToolsList.add(clientTools);
                    clientToolsMap.put(clientTools.getClientId(), oldToolsList);
                }
            } else {
                log.error("节点工具不存在");
            }
        });
        clientToolsMap.forEach((clientId, clientTools) -> {
            LiteFlowTask task = new LiteFlowTask();
            task.setTaskName("安装工具");
            task.setClientId(clientId);
            task.setRouter("1");
            task.setVersion(1);
            task.setUnFinishedChain(clientTools.size());
            task.setChainId("1816737239597858818");
            liteFlowTaskService.saveMain(task, null);
            List<LiteFlowSubTask> subTaskList = new ArrayList<>();
            clientTools.forEach(clientTool -> {
                LiteFlowSubTask subTask = new LiteFlowSubTask();
                subTask.setTaskId(task.getId());
                subTask.setVersion(1);
                subTask.setTaskStatus(LiteFlowStatusEnums.PENDING.name());
                subTask.setSubTaskParam(JSONObject.toJSONString(clientTool));
                subTaskList.add(subTask);
            });
            liteFlowSubTaskService.saveBatch(subTaskList);
        });
    }

    @Override
    public void changeStatus(String id, Boolean status) {
        ClientTools clientTools = getById(id);
        if (clientTools != null) {
            clientTools.setAutoInstall(status);
            updateById(clientTools);
        } else {
            log.error("节点工具不存在");
        }
    }

    @Override
    public List<ClientTools> getAntoInstallTools(String clientId) {
        LambdaQueryWrapper<ClientTools> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientTools::getAutoInstall, true);
        queryWrapper.eq(ClientTools::getClientId, clientId);
        return list(queryWrapper);
    }
}
