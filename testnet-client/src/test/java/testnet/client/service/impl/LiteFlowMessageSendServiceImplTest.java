package testnet.client.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.dto.IpOrSubDomainToPortDTO;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LiteFlowMessageSendServiceImplTest {

    @Resource
    private LiteFlowMessageSendServiceImpl liteFlowMessageSendService;
    @Test
    void sendResult() {
        liteFlowMessageSendService.setTaskId("1910687065034088449");

        IpOrSubDomainToPortDTO dto = new IpOrSubDomainToPortDTO();
        List<IpOrSubDomainToPortDTO.Port> portList = new ArrayList<>();
        IpOrSubDomainToPortDTO.Port port = new IpOrSubDomainToPortDTO.Port();
        port.setPort(80);
        port.setProtocol("tcp");
        port.setIp("127.0.0.1");
        portList.add(port);
        dto.setPortList(portList);
        liteFlowMessageSendService.sendResult(dto);
    }
}