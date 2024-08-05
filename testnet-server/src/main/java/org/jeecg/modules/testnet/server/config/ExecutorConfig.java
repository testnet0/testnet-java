/**
 * @program: testnet-jeecg
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;


@Configuration
public class ExecutorConfig {

    @Resource
    private Environment env;

    @Bean(name = "logMessageExecutor")
    public ThreadPoolTaskExecutor logMessageExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(getCorePoolSize() * 5);
        threadPoolTaskExecutor.setMaxPoolSize(getMaxPoolSize() * 5);
        threadPoolTaskExecutor.setThreadNamePrefix("log-consumer-");
        return threadPoolTaskExecutor;
    }

    // 线程池核心线程数
    public int getCorePoolSize() {
        int processNum = Runtime.getRuntime().availableProcessors();
        int defaultCorePoolSize = (int) (processNum / (1 - 0.2));
        return env.getProperty("pool.core.size", Integer.class, defaultCorePoolSize);
    }

    // 线程池最大线程数
    public int getMaxPoolSize() {
        int processNum = Runtime.getRuntime().availableProcessors();
        int defaultMaxPoolSize = (int) (processNum / (1 - 0.5));
        return env.getProperty("pool.max.size", Integer.class, defaultMaxPoolSize);
    }

}
