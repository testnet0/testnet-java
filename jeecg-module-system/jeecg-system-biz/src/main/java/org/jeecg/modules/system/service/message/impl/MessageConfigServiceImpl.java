package org.jeecg.modules.system.service.message.impl;



import org.jeecg.modules.system.mapper.MessageConfigMapper;
import org.jeecg.modules.system.message.MessageConfig;
import org.jeecg.modules.system.service.message.IMessageConfigService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 消息推送配置
 * @Author: jeecg-boot
 * @Date:   2024-09-27
 * @Version: V1.0
 */
@Service
public class MessageConfigServiceImpl extends ServiceImpl<MessageConfigMapper, MessageConfig> implements IMessageConfigService {

}
