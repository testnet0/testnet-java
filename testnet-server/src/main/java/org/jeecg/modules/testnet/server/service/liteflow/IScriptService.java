package org.jeecg.modules.testnet.server.service.liteflow;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;

import java.util.List;

/**
 * @Description: 脚本
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IScriptService extends IService<Script> {

    void saveScript(Script script);

    void updateScript(Script script);

    void saveOrUpdateScript(Script script);

    void deleteScript(String id);

    void copyScript(String scriptId);

    void changeStatus(String id, Boolean status);

    List<Script> needInstallScript();
}
