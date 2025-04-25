package org.jeecg.modules.message.handle.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.dto.message.MessageDTO;
import org.jeecg.modules.message.handle.ISendMsgHandle;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class WebhookMsgHandle implements ISendMsgHandle {

    @Override
    public void sendMsg(String esReceiver, String esTitle, String esContent) {

    }

    @Override
    public void sendMessage(MessageDTO messageDTO) {
        try {
            log.info("send webhook message, toUser is{}, title is{}, content is{}", messageDTO.getToUser(), messageDTO.getTitle(), messageDTO.getContent());
            URL url = new URL(messageDTO.getToUser());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = messageDTO.getContent().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                log.info("send webhook message success, response code is {}", responseCode);
            } else {
                log.error("send webhook message failed, response code is {}", responseCode);
            }
        } catch (Exception e) {
            log.error("send webhook message failed", e);
        }
    }
}
