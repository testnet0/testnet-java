/**
 * @program: testnet-client
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package testnet.client.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class VersionReportService {

    @Resource
    private ILiteFlowMessageSendService clientStreamSendService;

    @Scheduled(fixedRate = 2 * 1000L) // 每2秒执行一次
    public void clientVersion() {
        clientStreamSendService.sendVersion();
    }
}
