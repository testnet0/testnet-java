/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.schedule;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ClientStatusScheduler {


    @Resource
    private IClientService clientService;

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Scheduled(fixedRate = 600 * 1000L, initialDelay = 60 * 1000L) // 每60秒执行一次
    public void clientVersion() {
        List<Client> clients = clientService.getAllOnlineClients();
        for (Client client : clients) {
            Date currentTime = new Date(); // 当前时间
            Date updateTime = client.getUpdateTime(); // 客户端的更新时间
            if (updateTime == null) {
                clientService.updateById(client);
                return;
            }
            long timeDifferenceInMillis = currentTime.getTime() - updateTime.getTime(); // 时间差（毫秒）
            long timeDifferenceInSeconds = timeDifferenceInMillis / 1000;
            if (timeDifferenceInSeconds > 300) {
                client.setStatus("N"); // 更新状态为N
                log.info("Client :{} is offline ", client.getClientName());
                clientService.updateById(client);
                clientService.clearCache();
                Map<String, Object> params = new HashMap<>();
                params.put("clientName", client.getClientName());
                sysBaseApi.sendWebHookeMessage("客户端离线通知", params, "client_status_notify");
            }
        }
    }

}
