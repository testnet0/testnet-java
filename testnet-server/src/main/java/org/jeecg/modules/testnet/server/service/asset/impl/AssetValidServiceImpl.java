package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.ReflectHelper;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetBlackList;
import org.jeecg.modules.testnet.server.service.asset.IAssetBlackListService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public <T extends AssetBase> boolean isValid(T asset, AssetTypeEnums assetType) {
        String fieldNames = getValidFieldName(assetType);
        for (String fieldName : fieldNames.split(",")) {
            Object fieldValue = ReflectHelper.getFieldVal(fieldName, asset);
            if (fieldValue == null) {
                log.error("校验失败，资产类型：{} , 字段：{} 值为空！", assetType, fieldName);
                return false;
            }
            if (fieldValue.equals(String.class)) {
                if (fieldValue.toString().isEmpty()) {
                    log.error("校验失败，资产类型：{} , 字段：{}为空字符！", assetType, fieldName);
                    return false;
                }
            }
            if (fieldValue.equals(Integer.class)) {
                if (fieldValue.toString().equals("0")) {
                    log.error("校验失败，资产类型：{}, 字段：{} 值为0！", assetType, fieldName);
                    return false;
                }
            }
            List<AssetBlackList> assetBlackList = assetBlackListService.getBlackList(assetType.getCode());
            for (AssetBlackList blackList : assetBlackList) {
                if (fieldValue.toString().contains(blackList.getKeyword())) {
                    log.error("{} ：{} 命中黑名单", assetType, fieldValue);
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public <T extends AssetBase> T getUniqueAsset(T asset, IAssetService<T, ? extends AssetBase, ? extends AssetBase> assetService, AssetTypeEnums assetType) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(asset.getId())) {
            queryWrapper.ne("id", asset.getId());
        }
        Map<String, String> duplicateCheckFieldNames = getUniqueCheckFieldName(assetType);
        duplicateCheckFieldNames.forEach((k, v) -> {
            Object fieldValue = ReflectHelper.getFieldVal(v, asset);
            if (fieldValue != null) {
                queryWrapper.eq(k, fieldValue);
            } else {
                queryWrapper.eq(k, "");
            }
        });
        return assetService.getOne(queryWrapper);
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
                return "subDomain,domainId,projectId";
            case "ip":
                return "ip,projectId";
            case "port":
                return "ip,port,projectId";
            case "web":
                return "portId,projectId";
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
                map.put("port_id", "portId");
                map.put("domain", "domain");
                map.put("project_id", "projectId");
                break;
            case "vul":
                map.put("asset_type", "assetType");
                map.put("asset_id", "assetId");
                map.put("vul_name", "vulName");
                map.put("project_id", "projectId");
                break;
            case "api":
                map.put("absolute_path", "absolutePath");
                map.put("http_method", "httpMethod");
                map.put("project_id", "projectId");
                map.put("request_body", "requestBody");
                break;
        }
        return map;
    }


}
