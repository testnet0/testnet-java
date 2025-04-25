//package org.jeecg.modules.testnet.server.aspect;// 引入必要的依赖
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.parser.Feature;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.jeecg.common.api.vo.Result;
//import org.jeecg.common.util.oConvertUtils;
//import org.jeecg.modules.testnet.server.aspect.annotation.AssetDict;
//import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
//import org.jeecg.modules.testnet.server.service.asset.impl.IAssetCacheService;
//import org.springframework.stereotype.Component;
//import testnet.common.enums.AssetTypeEnums;
//
//import javax.annotation.Resource;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//@Aspect
//@Component
//@Slf4j
//public class AssetServiceAspect {
//
//    @Resource
//    private IAssetCacheService assetCacheService;
//
//    @Resource
//    private ObjectMapper objectMapper;
//
//    // 定义切点，拦截 IAssetCommonOptionService 接口中的所有方法
//    @Pointcut("execution(* org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService.*(..))")
//    public void assetServiceMethods() {
//    }
//
//    // 定义返回后通知，处理方法返回的结果
//    @AfterReturning(pointcut = "assetServiceMethods()", returning = "result")
//    public Object handleResult(Object result) {
//        long start = System.currentTimeMillis();
//        if (result instanceof IPage) {
//            List<Object> records = ((IPage) result).getRecords();
//            List<JSONObject> items = new ArrayList<>();
//            Boolean hasDict = checkHasDict(records);
//            if (!hasDict) {
//                return result;
//            }
//            records.forEach(record -> {
//                String json = "{}";
//                try {
//                    //update-begin--Author:zyf -- Date:20220531 ----for：【issues/#3629】 DictAspect Jackson序列化报错-----
//                    //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
//                    json = objectMapper.writeValueAsString(record);
//                    //update-end--Author:zyf -- Date:20220531 ----for：【issues/#3629】 DictAspect Jackson序列化报错-----
//                } catch (JsonProcessingException e) {
//                    log.error("json解析失败" + e.getMessage(), e);
//                }
//                //update-begin--Author:scott -- Date:20211223 ----for：【issues/3303】restcontroller返回json数据后key顺序错乱 -----
//                JSONObject item = JSONObject.parseObject(json, Feature.OrderedField);
//                for (Field field : oConvertUtils.getAllFields(record)) {
//                    AssetDict assetDict = field.getAnnotation(AssetDict.class);
//                    if (assetDict != null) {
//                        try {
//                            String id = item.getString(field.getName());
//                            AssetTypeEnums assetType = assetDict.assetType();
//                            String dictText = assetDict.dicText();
//                            // 查询对应的记录
//                            Result<? extends AssetBase> assetResult = assetCacheService.getAssetDOByIdAndAssetType(id, assetType);
//                            if (assetResult.isSuccess() && assetResult.getResult() != null) {
//                                // 通过反射获取dictText字段的值
//                                Field dictField = assetResult.getResult().getClass().getDeclaredField(dictText);
//                                dictField.setAccessible(true);
//                                String translateText = (String) dictField.get(assetResult.getResult());
//                                // 创建新的字段名
//                                String newFieldName = field.getName() + "_dictText";
//                                item.put(newFieldName, translateText);
//                                items.add(item);
//                            }
//                        } catch (Exception e) {
//                            log.error("Error processing @AssetDict annotation: ", e);
//                        }
//                    }
//                }
//            });
//            ((IPage) result).setRecords(items);
//
//        }
//        long end = System.currentTimeMillis();
//        log.info("注入耗时: {}ms", end - start);
//        return result;
//    }
//
//
//    /**
//     * 检测返回结果集中是否包含Dict注解
//     *
//     * @param records
//     * @return
//     */
//    private Boolean checkHasDict(List<Object> records) {
//        if (oConvertUtils.isNotEmpty(records) && !records.isEmpty()) {
//            for (Field field : oConvertUtils.getAllFields(records.get(0))) {
//                if (oConvertUtils.isNotEmpty(field.getAnnotation(AssetDict.class))) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//}
