package testnet.common.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import testnet.common.service.IRedisStreamService;

import javax.annotation.Resource;


/**
 * redis操作实现类
 */
@Service
@Slf4j
public class RedisStreamServiceImpl implements IRedisStreamService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String addObject(String key, Object o) {
        ObjectRecord<String, Object> record = StreamRecords.newRecord()
                .in(key)
                .ofObject(o)
                .withId(RecordId.autoGenerate());
        RecordId recordId = redisTemplate.opsForStream()
                .add(record);
        if (recordId != null) {
            log.trace("recordId:{}", recordId);
            return recordId.getValue();
        } else {
            return "";
        }
    }

    @Override
    public Long del(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    @Override
    public void initKeyAndGroup(String channelName, String groupName) {
        log.info("初始化redis key ：{}", channelName);
        // 初始化消费组和key
        try {
            String redisId = addObject(channelName, "test");
            log.info("初始化消费组成功！记录ID：{}", redisId);
            del(channelName, redisId);
            String result = redisTemplate.opsForStream().createGroup(channelName, groupName);
            log.info("消费组 {} 创建：{}", "group", result);
        } catch (Exception e) {
            // log.info("消费组创建失败:{}", e.getMessage());
        }
    }

    @Override
    public Long ack(String key, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }


}
