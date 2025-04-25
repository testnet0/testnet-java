package org.jeecg.modules.testnet.server.service.asset.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.ReflectHelper;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetBlackList;
import org.jeecg.modules.testnet.server.service.asset.IAssetBlackListService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.utils.HashUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AssetValidServiceImpl implements IAssetValidService {


    @Resource
    private IAssetBlackListService assetBlackListService;


    /**
     * 验证资产是否合法
     *
     * @param asset     资产
     * @param assetType 资产类型
     * @return true:合法 false:非法
     */
    @Override
    public <T extends AssetBase> Result<T> isValid(T asset, AssetTypeEnums assetType) {
        String fieldNames = getValidFieldName(assetType);
        for (String fieldName : fieldNames.split(",")) {
            Object fieldValue = ReflectHelper.getFieldVal(fieldName, asset);
            if (fieldValue == null) {
                log.error("校验失败，资产类型：{} , 字段：{} 值为空！", assetType, fieldName);
                return Result.error("校验失败, 字段：" + fieldName + " 为空！");
            }
            if (fieldValue.equals(String.class)) {
                if (fieldValue.toString().isEmpty()) {
                    log.error("校验失败，资产类型：{} , 字段：{}为空字符！", assetType, fieldName);
                    return Result.error("校验失败, 字段：" + fieldName + " 为空字符！");
                }
            }
            if (fieldValue.equals(Integer.class)) {
                if (fieldValue.toString().equals("0")) {
                    log.error("校验失败，资产类型：{}, 字段：{} 值为0！", assetType, fieldName);
                    return Result.error("校验失败, 字段：" + fieldName + " 值为0！");
                }
            }
            List<AssetBlackList> assetBlackList = assetBlackListService.getBlackList(assetType.getCode());
            for (AssetBlackList blackList : assetBlackList) {
                if (blackList.getBlacklistType().equals("keyword")) {
                    if (fieldValue.toString().contains(blackList.getKeyword())) {
                        log.error("{} ：{} 命中黑名单", assetType, fieldValue);
                        return Result.error("校验失败, 字段：" + fieldValue + " 命中关键字黑名单:" + blackList.getKeyword());
                    }
                } else {
                    // 通过正则匹配
                    Pattern pattern = Pattern.compile(blackList.getKeyword());
                    Matcher matcher = pattern.matcher(fieldValue.toString());
                    if (matcher.find()) {
                        log.error("{} ：{} 命中黑名单", assetType, fieldValue);
                        return Result.error("校验失败, 字段：" + fieldValue + " 命中正则黑名单:" + blackList.getKeyword());
                    }
                }
            }
        }
        return Result.OK();
    }


    /**
     * 获取资产校验字段名
     *
     * @param assetType 资产类型
     * @return fieldNames 字段名 逗号分割
     */
    private String getValidFieldName(AssetTypeEnums assetType) {
        String code = assetType.getCode();
        switch (code) {
            case "company":
                return "companyName,projectId";
            case "domain":
                return "domain,projectId";
            case "sub_domain":
                return "subDomain,projectId";
            case "ip":
                return "ip,projectId";
            case "port":
                return "ip,port,projectId";
            case "web":
                return "webUrl,projectId";
            case "vul":
                return "vulName,severity,vulStatus,assetId,assetType";
            case "api":
                return "absolutePath,httpMethod,projectId";
            default:
                return null;
        }
    }


    @Override
    public Map<String, String> getUniqueCheckFieldName(AssetTypeEnums assetType) {
        String code = assetType.getCode();
        HashMap<String, String> map = new HashMap<>();
        switch (code) {
            case "company":
                map.put("company_name", "companyName");
                map.put("project_id", "projectId");
                break;
            case "domain":
                map.put("domain", "domain");
                map.put("project_id", "projectId");
                break;
            case "ip":
                map.put("ip", "ip");
                map.put("project_id", "projectId");
                break;
            case "port":
                map.put("port", "port");
                map.put("ip", "ip");
                map.put("project_id", "projectId");
                break;
            case "sub_domain":
                map.put("sub_domain", "subDomain");
                map.put("project_id", "projectId");
                break;
            case "web":
                map.put("web_url", "webUrl");
                break;
            case "vul":
                map.put("asset_type", "assetType");
                map.put("asset_id", "assetId");
                map.put("vul_name", "vulName");
                map.put("project_id", "projectId");
                break;
            case "api":
                map.put("path_md5", "pathMd5");
                break;
        }
        return map;
    }

    @SneakyThrows
    @Override
    public <D extends AssetBase> String getShaKey(D asset, AssetTypeEnums assetType) {
        StringBuilder sb = new StringBuilder();
        for (String fieldName : getValidFieldName(assetType).split(",")) {
            Object fieldValue = ReflectHelper.getFieldVal(fieldName, asset);
            sb.append(fieldName).append("=");
            if (fieldValue != null) {
                sb.append(fieldValue);
            }
        }
        return HashUtils.calculateSHA256(sb.toString());
    }


}
