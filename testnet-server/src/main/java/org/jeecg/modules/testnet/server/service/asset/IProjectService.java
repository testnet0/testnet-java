package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.Project;

/**
 * @Description: 项目
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IProjectService extends IService<Project> {

    Project getByProjectIdOrName(String projectIdOrName);


    void cleanCache(String projectIdOrName);
}
