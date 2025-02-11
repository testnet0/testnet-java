package org.jeecg.modules.testnet.server.service.system;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.system.InstallFlag;

/**
 * @Description: 安装记录表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IInstallFlagService extends IService<InstallFlag> {
    InstallFlag getFlag();
}
