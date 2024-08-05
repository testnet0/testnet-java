package org.jeecg.modules.testnet.server.service.liteflow.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.service.ISysDataLogService;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;
import org.jeecg.modules.testnet.server.mapper.liteflow.ScriptMapper;
import org.jeecg.modules.testnet.server.service.liteflow.IScriptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 脚本
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class ScriptServiceImpl extends ServiceImpl<ScriptMapper, Script> implements IScriptService {

    @Resource
    private ISysDataLogService sysDataLogService;

    @Override
    public void saveScript(Script script) {
        save(script);
    }

    @Override
    public void updateScript(Script script) {
        sysDataLogService.addDataLog("lite_flow_script", script.getId(), script.getScriptData());
        updateById(script);
    }

    @Override
    public void saveOrUpdateScript(Script script) {

    }

    @Override
    public void deleteScript(String id) {
        removeById(id);
    }

    @Override
    public void copyScript(String scriptId) {
        Script script = getById(scriptId);
        if (script != null) {
            script.setId(null);
            script.setScriptName(script.getScriptName() + "_copy");
            script.setScriptId(script.getScriptId() + "_copy");
            script.setCreateTime(new Date());
            script.setUpdateTime(null);
            save(script);
        }
    }

    @Override
    public void changeStatus(String id, Boolean status) {
        Script script = getById(id);
        script.setEnable(status);
        updateById(script);
    }

    @Override
    public List<Script> needInstallScript() {
        LambdaQueryWrapper<Script> query = new LambdaQueryWrapper<>();
        query.eq(Script::getEnable, true);
        query.eq(Script::getNeedInstall, true);
        return list(query);
    }

}
