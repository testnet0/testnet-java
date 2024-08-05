package testnet.client.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InitConfigService {

    private final JdbcTemplate jdbcTemplate;
    @Resource
    private EnvConfig envConfig;

    @Autowired
    public InitConfigService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostConstruct
    public void init() {
        initConfigFile();
    }

    private void initConfigFile() {
        log.info("初始化配置文件");
        List<Map<String, Object>> configFile = jdbcTemplate.queryForList(
                "SELECT cc.config,cc.config_path FROM client c LEFT JOIN client_config cc ON cc.client_id = c.id WHERE c.client_name = ? AND cc.config_file = 'Y'",
                envConfig.getClientName()
        );
        for (Map<String, Object> config : configFile) {
            String configPath = (String) config.get("config_path");
            String configContent = (String) config.get("config");
            log.info("配置文件路径:{}", configPath);
            File file = new File(configPath);
            try {
                FileUtils.writeStringToFile(file, configContent, StandardCharsets.UTF_8, false);
            } catch (IOException e) {
                log.error("配置文件写入失败, 错误信息: {}", e.getMessage());
            }
        }
    }
}