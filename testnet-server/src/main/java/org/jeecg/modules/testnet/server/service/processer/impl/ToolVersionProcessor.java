/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.client.ClientTools;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.client.IClientToolsService;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.stereotype.Service;
import testnet.common.entity.liteflow.ClientToolVersion;
import testnet.common.entity.liteflow.LiteFlowResult;

import javax.annotation.Resource;

@Service
@Slf4j
public class ToolVersionProcessor implements IAssetResultProcessorService {

    @Resource
    private IClientToolsService clientToolsService;


    @Override
    public void processAsset(String baseAssetId, String source, LiteFlowTask liteFlowTask, LiteFlowSubTask liteFlowSubTask, LiteFlowResult resultBase) {
        ClientToolVersion clientToolVersion = JSONObject.parseObject(resultBase.getResult(), ClientToolVersion.class);
        ClientTools clientTools = JSONObject.parseObject(liteFlowSubTask.getSubTaskParam(), ClientTools.class);
        if (clientToolVersion != null) {
            clientTools.setVersion(clientToolVersion.getToolVersion());
            clientTools.setStatus(true);
            clientToolsService.updateById(clientTools);
        }
    }
}
