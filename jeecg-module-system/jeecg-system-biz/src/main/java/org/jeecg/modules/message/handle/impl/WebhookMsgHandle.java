package org.jeecg.modules.message.handle.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.dto.message.MessageDTO;
import org.jeecg.modules.message.handle.ISendMsgHandle;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebhookMsgHandle implements ISendMsgHandle {

    @Override
    public void sendMsg(String esReceiver, String esTitle, String esContent) {

    }

    @Override
    public void sendMessage(MessageDTO messageDTO) {
        log.info("send webhook message,toUser is{},title is{},content is{}", messageDTO.getToUser(), messageDTO.getTitle(), messageDTO.getContent());
        String body = HttpRequest.post(messageDTO.getToUser()).header("Content-Type", "application/json").body(messageDTO.getContent()).execute().body();
        log.info("send webhook message success,response is{}", body);
    }
}
