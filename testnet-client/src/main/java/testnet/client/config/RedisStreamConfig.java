/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package testnet.client.config;

import io.lettuce.core.RedisCommandExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.service.IRedisStreamService;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConfig {


    @Resource
    private IRedisStreamService redisStreamService;

    @Resource
    private EnvConfig envConfig;


    private StreamMessageListenerContainer<String, ObjectRecord<String, TaskExecuteMessage>> taskExecuteContainer;


    @Bean
    public StreamListener<String, ObjectRecord<String, TaskExecuteMessage>> taskExecuteListener() {
        return new TaskExecuteMessageListener();
    }

    @Bean
    public Subscription taskExecuteStreamSubscription(RedisConnectionFactory connectionFactory) {
        redisStreamService.initKeyAndGroup(Constants.STREAM_KEY_TASK_EXECUTE + envConfig.getClientName(), Constants.STREAM_CLIENT_GROUP);
        StreamOffset<String> streamOffset = StreamOffset.create(Constants.STREAM_KEY_TASK_EXECUTE + envConfig.getClientName(), ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                ObjectRecord<String, TaskExecuteMessage>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .executor(Executors.newSingleThreadExecutor())
                .batchSize(2)
                .pollTimeout(Duration.ofMillis(1000))
                .targetType(TaskExecuteMessage.class)
                .build();

        taskExecuteContainer =
                StreamMessageListenerContainer
                        .create(connectionFactory, options);

        StreamMessageListenerContainer.ConsumerStreamReadRequest<String> request = StreamMessageListenerContainer.StreamReadRequest
                .builder(streamOffset)
                .consumer(Consumer.from(Constants.STREAM_CLIENT_GROUP, envConfig.getClientName()))
                .cancelOnError(throwable -> {
                    if(throwable instanceof RedisSystemException && throwable.getCause().getMessage().contains("NOGROUP")){
                        redisStreamService.initKeyAndGroup(Constants.STREAM_KEY_TASK_EXECUTE + envConfig.getClientName(), Constants.STREAM_CLIENT_GROUP);
                        return false;
                    }
                    return true;
                })
                .autoAcknowledge(false)
                .build();
        Subscription subscription = taskExecuteContainer.register(request, taskExecuteListener());
        taskExecuteContainer.start();
        return subscription;

    }
}

