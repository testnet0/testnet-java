package testnet.client;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
@ComponentScan(value = {"testnet.client", "testnet.common"})
public class TestnetClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestnetClientApplication.class, args);
    }

}
