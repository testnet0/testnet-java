/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.controller.liteflow;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.liteflow.impl.BatchRunChain;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "流程管理")
@RestController
@RequestMapping("/testnet.server/chain")
@Slf4j
public class RunChainController {

    @Resource
    private BatchRunChain batchRunChain;

    @Resource
    private IClientService clientService;

    /**
     * 运行指定的工作流
     *
     * @param params
     * @return
     */
    @Operation(summary = "流程管理-批量运行工作流")
    @RequestMapping(value = "/batchRunTargetChain", method = {RequestMethod.POST})
    @RequiresPermissions("testnet.server:chain:batchRunTargetChain")
    public Result<String> batchRunTargetChain(@RequestBody String params) {
        List<Client> clients = clientService.getAllOnlineClients();
        if (clients == null || clients.isEmpty()) {
            return Result.error("批量扫描任务创建失败,没有在线的客户端！");
        }
        batchRunChain.batchRun(params, 1);
        return Result.OK("任务执行成功，请去工作流管理-任务列表查看任务状态");
    }
}
