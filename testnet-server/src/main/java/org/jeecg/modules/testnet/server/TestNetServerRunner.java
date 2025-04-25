package org.jeecg.modules.testnet.server;


import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.testnet.server.entity.system.InstallFlag;
import org.jeecg.modules.testnet.server.service.system.IInstallFlagService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Arrays;

@Component
@Slf4j
public class TestNetServerRunner implements ApplicationRunner, DisposableBean {


    @Resource
    private IInstallFlagService installFlagService;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private Environment env;

    @Override
    public void run(ApplicationArguments args) {
        // 判断当前启动模式
        if (!isProductionEnvironment()) {
            log.info("当前环境不是生产环境，跳过初始化...");
            return;
        }
        InstallFlag installFlag = installFlagService.getFlag();
        if (installFlag.getInstalled().equals(0)) {
            log.info("首次安装，开始初始化...");
            String password = generateRandomPassword(8);
            log.info("随机密码：{}", password);
            sysUserService.randomPassword(password);
            installFlag.setInstalled(1);
            installFlagService.saveOrUpdate(installFlag);
        }
    }

    private boolean isProductionEnvironment() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    @Override
    public void destroy() {

    }

    private String generateRandomPassword(int length) {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";

        String allCharacters = upperCaseLetters + lowerCaseLetters + digits;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(index));
        }
        return password.toString();
    }
}