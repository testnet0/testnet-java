package org.jeecg.config;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.dict.service.AutoPoiDictMapServiceI;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 描述：AutoPoi Excel注解支持字典参数设置
 * 举例： @Excel(name = "性别", width = 15, dicCode = "sex")
 * 1、导出的时候会根据字典配置，把值1,2翻译成：男、女;
 * 2、导入的时候，会把男、女翻译成1,2存进数据库;
 *
 * @Author:TestNet
 * @since：2024-09-09
 * @Version:1.0
 */
@Slf4j
@Service
public class AutoPoiDictMapConfig implements AutoPoiDictMapServiceI {

    @Lazy
    @Resource
    private CommonAPI commonApi;

    /**
     * 通过字典查询easypoi，所需字典文本
     *
     * @return
     * @Author:TestNet
     * @since：2024-09-09
     */
    public HashMap<String, String> queryDict(String dicTable, String dicCode, String dicText, boolean isKeyValue) {
        HashMap<String, String> dictReplaces = new HashMap<>();
        List<DictModel> dictList = null;
        // step.1 如果没有字典表则使用系统字典表
        if (oConvertUtils.isEmpty(dicTable)) {
            dictList = commonApi.queryDictItemsByCode(dicCode);
        } else {
            try {
                dicText = oConvertUtils.getString(dicText, dicCode);
                dictList = commonApi.queryTableDictItemsByCode(dicTable, dicText, dicCode);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }


        for (DictModel t : dictList) {
            //update-begin---author:liusq   Date:20230517  for：[issues/4917]excel 导出异常---
            if (t != null && t.getText() != null && t.getValue() != null) {
                if (isKeyValue) {
                    dictReplaces.put(t.getValue(), t.getText());
                } else {
                    dictReplaces.put(t.getText(), t.getValue());
                }
            }
        }
        if (!dictReplaces.isEmpty()) {
            log.debug("---AutoPoi--Get_DB_Dict------{}", dictReplaces);
            return dictReplaces;
        }
        return null;
    }
}
