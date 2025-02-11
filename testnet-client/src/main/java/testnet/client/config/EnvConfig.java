/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package testnet.client.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EnvConfig {
    @Resource
    private Environment env;

    @Value("${testnet.client.version}")
    private String clientVersion;

    @Value("${testnet.client.name}")
    private String clientName;

    @Value("${testnet.result.path}")
    private String resultPath;


    // 客户端名称
    public String getClientName() {
        return env.getProperty("testnet.client.name", clientName);
    }

    // 客户端版本号
    public String getClientVersion() {
        return env.getProperty("testnet.client.version", clientVersion);
    }

    public String getResultPath() {
        return env.getProperty("testnet.result.path", resultPath);
    }

}

