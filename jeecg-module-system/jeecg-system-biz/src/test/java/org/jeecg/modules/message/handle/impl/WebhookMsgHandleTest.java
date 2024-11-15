package org.jeecg.modules.message.handle.impl;

import org.jeecg.common.api.dto.message.MessageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = WebhookMsgHandle.class)
class WebhookMsgHandleTest {

    @Resource
    private WebhookMsgHandle webhookMsgHandle;
    @Test
    void sendMessage() {

    }
}