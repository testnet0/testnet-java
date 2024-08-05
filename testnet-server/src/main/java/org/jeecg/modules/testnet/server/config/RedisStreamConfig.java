/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.listener.LogMessageListener;
import org.jeecg.modules.testnet.server.listener.ResultMessageListener;
import org.jeecg.modules.testnet.server.listener.StatusMessageListener;
import org.jeecg.modules.testnet.server.listener.VersionMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.ClientStatus;
import testnet.common.entity.liteflow.LiteFlowResult;
import testnet.common.entity.liteflow.LogMessage;
import testnet.common.entity.liteflow.VersionMessage;
import testnet.common.service.IRedisStreamService;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConfig {


    @Resource
    private IRedisStreamService redisStreamService;

    private StreamMessageListenerContainer<String, ObjectRecord<String, LogMessage>> logMessageContainer;
    private StreamMessageListenerContainer<String, ObjectRecord<String, ClientStatus>> statusMessageContainer;
    private StreamMessageListenerContainer<String, ObjectRecord<String, VersionMessage>> versionMessageContainer;
    private StreamMessageListenerContainer<String, ObjectRecord<String, LiteFlowResult>> resultMessageContainer;


    @Bean
    public StreamListener<String, ObjectRecord<String, LogMessage>> logStreamListener() {
        return new LogMessageListener();
    }

    @Bean
    public StreamListener<String, ObjectRecord<String, ClientStatus>> statusStreamListener() {
        return new StatusMessageListener();
    }

    @Bean
    public StreamListener<String, ObjectRecord<String, VersionMessage>> versionStreamListener() {
        return new VersionMessageListener();
    }

    @Bean
    public StreamListener<String, ObjectRecord<String, LiteFlowResult>> resultStreamListener() {
        return new ResultMessageListener();
    }

    @Bean
    public Subscription logStreamSubscription(RedisConnectionFactory connectionFactory)
            throws UnknownHostException {
        redisStreamService.initKeyAndGroup(Constants.STREAM_KEY_LOG, Constants.STREAM_CLIENT_GROUP);
        StreamOffset<String> streamOffset = StreamOffset.create(Constants.STREAM_KEY_LOG, ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                ObjectRecord<String, LogMessage>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(1000))
                .executor(Executors.newSingleThreadExecutor())
                .batchSize(2)
                .targetType(LogMessage.class)
                .build();

        logMessageContainer =
                StreamMessageListenerContainer
                        .create(connectionFactory, options);
        StreamMessageListenerContainer.ConsumerStreamReadRequest<String> request = StreamMessageListenerContainer.StreamReadRequest
                .builder(streamOffset)
                .consumer(Consumer.from(Constants.STREAM_CLIENT_GROUP, InetAddress.getLocalHost().getHostName()))
                .cancelOnError(throwable -> !(throwable instanceof QueryTimeoutException))
                .autoAcknowledge(false)
                .build();

        Subscription subscription = logMessageContainer.register(request, logStreamListener());
        logMessageContainer.start();
        return subscription;
    }

    @Bean
    public Subscription statusStreamSubscription(RedisConnectionFactory connectionFactory)
            throws UnknownHostException {
        redisStreamService.initKeyAndGroup(Constants.STREAM_KEY_STATUS, Constants.STREAM_CLIENT_GROUP);
        StreamOffset<String> streamOffset = StreamOffset.create(Constants.STREAM_KEY_STATUS, ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                ObjectRecord<String, ClientStatus>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .executor(Executors.newSingleThreadExecutor())
                .batchSize(1)
                .pollTimeout(Duration.ofMillis(1000))
                .targetType(ClientStatus.class)
                .build();

        statusMessageContainer =
                StreamMessageListenerContainer
                        .create(connectionFactory, options);

        StreamMessageListenerContainer.ConsumerStreamReadRequest<String> request = StreamMessageListenerContainer.StreamReadRequest
                .builder(streamOffset)
                .consumer(Consumer.from(Constants.STREAM_CLIENT_GROUP, InetAddress.getLocalHost().getHostName()))
                .cancelOnError(throwable -> !(throwable instanceof QueryTimeoutException))
                .autoAcknowledge(false)
                .build();
        Subscription subscription = statusMessageContainer.register(request, statusStreamListener());
        statusMessageContainer.start();
        return subscription;

    }

    @Bean
    public Subscription versionStreamSubscription(RedisConnectionFactory connectionFactory)
            throws UnknownHostException {
        redisStreamService.initKeyAndGroup(Constants.STREAM_KEY_VERSION, Constants.STREAM_CLIENT_GROUP);
        StreamOffset<String> streamOffset = StreamOffset.create(Constants.STREAM_KEY_VERSION, ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                ObjectRecord<String, VersionMessage>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .executor(Executors.newSingleThreadExecutor())
                .batchSize(1)
                .pollTimeout(Duration.ofMillis(1000))
                .targetType(VersionMessage.class)
                .build();

        versionMessageContainer =
                StreamMessageListenerContainer
                        .create(connectionFactory, options);

        StreamMessageListenerContainer.ConsumerStreamReadRequest<String> request = StreamMessageListenerContainer.StreamReadRequest
                .builder(streamOffset)
                .consumer(Consumer.from(Constants.STREAM_CLIENT_GROUP, InetAddress.getLocalHost().getHostName()))
                .cancelOnError(throwable -> !(throwable instanceof QueryTimeoutException))
                .autoAcknowledge(false)
                .build();

        Subscription subscription = versionMessageContainer.register(request, versionStreamListener());
        versionMessageContainer.start();
        return subscription;

    }


    @Bean
    public Subscription resultStreamSubscription(RedisConnectionFactory connectionFactory)
            throws UnknownHostException {
        redisStreamService.initKeyAndGroup(Constants.STREAM_KEY_RESULT, Constants.STREAM_CLIENT_GROUP);
        StreamOffset<String> streamOffset = StreamOffset.create(Constants.STREAM_KEY_RESULT, ReadOffset.lastConsumed());

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String,
                ObjectRecord<String, LiteFlowResult>> options = StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                .executor(Executors.newSingleThreadExecutor())
                .batchSize(1)
                .pollTimeout(Duration.ofMillis(1000))
                .targetType(LiteFlowResult.class)
                .build();

        resultMessageContainer =
                StreamMessageListenerContainer
                        .create(connectionFactory, options);

        StreamMessageListenerContainer.ConsumerStreamReadRequest<String> request = StreamMessageListenerContainer.StreamReadRequest
                .builder(streamOffset)
                .consumer(Consumer.from(Constants.STREAM_CLIENT_GROUP, InetAddress.getLocalHost().getHostName()))
                .cancelOnError(throwable -> !(throwable instanceof QueryTimeoutException))
                .autoAcknowledge(false)
                .build();

        Subscription subscription = resultMessageContainer.register(request, resultStreamListener());
        resultMessageContainer.start();
        return subscription;

    }


    @PreDestroy
    public void onContainerShutdown() {
        if (logMessageContainer != null) {
            logMessageContainer.stop();
        }
        if (statusMessageContainer != null) {
            statusMessageContainer.stop();
        }
        if (versionMessageContainer != null) {
            versionMessageContainer.stop();
        }

        if (resultMessageContainer != null) {
            resultMessageContainer.stop();
        }
    }
}

