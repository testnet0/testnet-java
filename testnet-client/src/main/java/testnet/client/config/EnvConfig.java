/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package testnet.client.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class EnvConfig {
    @Resource
    private Environment env;

    // 客户端版本号
    @Getter
    @Value("${testnet.client.version}")
    private String clientVersion;

    @Value("${testnet.client.name}")
    private String clientName;

    @Value("${testnet.result.path}")
    private String resultPath;


    // 客户端名称
    public String getClientName() {
        String clientNameFromEnv = env.getProperty("TESTNET_CLIENT_NAME");
        return StringUtils.isNotBlank(clientNameFromEnv) ? clientNameFromEnv : this.clientName;
    }

    public String getResultPath() {
        String resultPath = env.getProperty("TESTNET_RESULT_PATH");
        return StringUtils.isNotBlank(resultPath) ? resultPath : this.resultPath;
    }

}

