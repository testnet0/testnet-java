package org.jeecg.modules.testnet.server.service.system.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.testnet.server.entity.system.InstallFlag;
import org.jeecg.modules.testnet.server.mapper.system.InstallFlagMapper;
import org.jeecg.modules.testnet.server.service.system.IInstallFlagService;
import org.springframework.stereotype.Service;

/**
 * @Description: 安装记录表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class InstallFlagServiceImpl extends ServiceImpl<InstallFlagMapper, InstallFlag> implements IInstallFlagService {

    @Override
    public InstallFlag getFlag() {
        return getOne(null);
    }
}
