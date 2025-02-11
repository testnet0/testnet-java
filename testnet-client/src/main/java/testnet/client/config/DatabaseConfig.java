package testnet.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    public String getDatabaseUrl() {
        return databaseUrl;
    }
}
