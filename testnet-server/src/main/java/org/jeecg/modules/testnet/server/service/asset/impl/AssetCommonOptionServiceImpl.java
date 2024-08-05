/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-06-27
 **/
package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.testnet.server.entity.asset.*;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTaskAsset;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskAssetMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IAssetLabelService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.springframework.stereotype.Service;
import testnet.common.constan.Constants;
import testnet.common.entity.liteflow.LogMessage;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
@SuppressWarnings("unchecked")
public class AssetCommonOptionServiceImpl implements IAssetCommonOptionService {

    @Resource
    private AssetCompanyServiceImpl assetCompanyService;

    @Resource
    private AssetDomainServiceImpl assetDomainService;


    @Resource
    private AssetSubDomainServiceImpl assetSubDomainService;

    @Resource
    private AssetIpServiceImpl assetIpService;

    @Resource
    private AssetPortServiceImpl assetPortService;

    @Resource
    private AssetVulServiceImpl assetVulService;

    @Resource
    private AssetWebServiceImpl assetWebService;

    @Resource
    private IAssetValidService assetValidService;

    @Resource
    private IAssetLabelService assetLabelService;

    @Resource
    private AssetApiServiceImpl assetApiService;

    @Resource
    private LiteFlowTaskAssetMapper liteFlowTaskAssetMapper;

    @Resource
    private JeecgElasticsearchTemplate elasticsearchTemplate;


    @Override
    public <T extends AssetBase> List<String> queryByAssetType(String params, String assetType) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        JSONObject query = jsonObject.getJSONObject("queryParam");
        String asset = jsonObject.getString("queryObject");
        List<String> assetsList = new ArrayList<>();
        Map<String, String[]> queryMap = new HashMap<>();
        if (query != null) {
            query.forEach((k, v) -> {
                if (v instanceof JSONArray) {
                    JSONArray jsonArray1 = (JSONArray) v;
                    List<String> list = new ArrayList<>();
                    jsonArray1.forEach(o -> list.add(o.toString()));
                    queryMap.put(k, list.toArray(new String[0]));
                } else if (v instanceof String) {
                    queryMap.put(k, new String[]{v.toString()});
                }
            });
        }
        try {
            Class<T> assetClass = (Class<T>) getAssetClassByType(AssetTypeEnums.fromCode(assetType));
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(AssetTypeEnums.fromCode(assetType));
            if (assetClass != null && assetService != null) {
                T assetInstance = JSONObject.parseObject(asset, assetClass);
                QueryWrapper<T> queryWrapper;
                if (assetInstance != null) {
                    queryWrapper = QueryGenerator.initQueryWrapper(assetInstance, queryMap);
                } else {
                    queryWrapper = QueryGenerator.initQueryWrapper(assetClass.getDeclaredConstructor().newInstance(), queryMap);
                }
                List<T> assets = assetService.list(queryWrapper);
                if (assets != null && !assets.isEmpty()) {
                    assets.forEach(o -> assetsList.add(JSONObject.toJSONString(assetService.convertDTO(o))));
                }
                return assetsList;
            }
        } catch (Exception e) {
            log.error("查询资产类型错误:{}", e.getMessage());
        }
        return assetsList;
    }

    @Override
    public <T extends AssetBase> void delByIdAndAssetType(String assetId, AssetTypeEnums assetType) {
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                assetService.delRelation(Arrays.asList(assetId.split(",")));
            }
        } catch (Exception e) {
            log.error("删除资产类型：{} 错误:{}", assetType, e.getMessage());
        }
    }

    @Override
    public <T extends AssetBase> void delAssetByProjectId(String projectId, AssetTypeEnums assetType) {
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<T> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id", projectId);
                assetService.remove(queryWrapper);
            }
        } catch (Exception e) {
            log.error("删除项目失败，资产类型：{} 错误:{}", assetType, e.getMessage());
        }
    }

    @Override
    public <T extends AssetBase> long getCountByProjectId(String projectId, AssetTypeEnums assetType) {
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<T> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id", projectId);
                return assetService.count(queryWrapper);
            }
        } catch (Exception e) {
            log.error("获取项目关联资产数量失败，资产类型：{} 错误:{}", assetType, e.getMessage());
        }
        return 0;
    }

    @Override
    public <T extends AssetBase, R extends AssetBase> IPage<R> page(T asset, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap, AssetTypeEnums assetType) {
        try {
            IAssetService<T, R, T> assetService = (IAssetService<T, R, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(asset, parameterMap);
                IPage<T> pageInfo = new Page<>(pageNo, pageSize);
                IPage<T> pageData = assetService.page(pageInfo, queryWrapper, parameterMap);
                IPage<R> pageVO = new Page<>();
                BeanUtil.copyProperties(pageData, pageVO);
                List<R> assetsList = new ArrayList<>();
                if (pageData.getRecords() != null) {
                    pageData.getRecords().forEach(record -> {
                        assetsList.add(assetService.convertVO(record));
                    });
                    pageVO.setRecords(assetsList);
                }
                return pageVO;
            }
        } catch (Exception e) {
            log.error("查询资产分页错误:{}", e.getMessage());
        }
        return null;
    }

    @Override
    public <T extends AssetBase> T addAssetByType(T asset, AssetTypeEnums assetType) {
        return addAssetByType(asset, assetType, true);
    }


    @Override
    public <T extends AssetBase> T getByIdAndAssetType(String id, AssetTypeEnums assetType) {
        IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
        if (assetService != null) {
            return assetService.getById(id);
        }
        return null;
    }


    @Override
    public <T extends AssetBase> T getByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType) {
        IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
        if (assetService != null) {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            fieldAndValue.forEach(queryWrapper::eq);
            return assetService.getOne(queryWrapper);
        }
        return null;
    }

    @Override
    public <T extends AssetBase> List<T> listByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType) {
        IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
        if (assetService != null) {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            fieldAndValue.forEach(queryWrapper::eq);
            return assetService.list(queryWrapper);
        }
        return null;
    }

    @Override
    public <T extends AssetBase, R extends AssetBase> R getDTOByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType) {
        IAssetService<T, T, R> assetService = (IAssetService<T, T, R>) getAssetServiceByType(assetType);
        if (assetService != null) {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            fieldAndValue.forEach(queryWrapper::eq);
            T asset = assetService.getOne(queryWrapper);
            if (asset != null) {
                return assetService.convertDTO(asset);
            }
        }
        return null;
    }

    @Override
    public <T extends AssetBase> T addAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate) {
        if (!assetValidService.isValid(asset, assetType)) {
            return null;
        }
        if (checkDuplicate && assetValidService.getUniqueAsset(asset, getAssetServiceByType(assetType), assetType) != null) {
            log.error("添加:{}, 资产：{}错误，存在重复值！", assetType, asset);
            return null;
        }
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            Class<T> assetClass = (Class<T>) getAssetClassByType(assetType);
            if (assetClass != null && assetService != null) {
                if (assetService.addAssetByType(asset)) {
                    return asset;
                }
            }
        } catch (Exception e) {
            log.error("添加资产发生错误:{}", e.getMessage());
        }
        return null;
    }


    @Override
    public <T extends AssetBase> T updateAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate) {
        if (!assetValidService.isValid(asset, assetType)) {
            return null;
        }
        if (checkDuplicate && assetValidService.getUniqueAsset(asset, getAssetServiceByType(assetType), assetType) != null) {
            log.error("更新:{}, 资产：{}错误，存在重复值！", assetType, asset);
            return null;
        }
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            Class<T> assetClass = (Class<T>) getAssetClassByType(assetType);
            if (assetClass != null && assetService != null) {
                if (assetService.updateAssetByType(asset)) {
                    return asset;
                }
            }
        } catch (Exception e) {
            log.error("更新资产类型错误:{}", e.getMessage());
        }
        return null;
    }

    @Override
    public <T extends AssetBase> T updateAssetByType(T asset, AssetTypeEnums assetType) {
        return updateAssetByType(asset, assetType, true);
    }

    @Override
    public <T extends AssetBase> T addOrUpdate(T asset, AssetTypeEnums assetType, String taskId, String subTaskId) {
        LogMessage logMessage = new LogMessage();
        logMessage.setTaskId(subTaskId);
        logMessage.setLevel("INFO");
        logMessage.setClientName("server");
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                T oldAsset;
                if (StringUtils.isNotBlank(asset.getId())) {
                    oldAsset = getByIdAndAssetType(asset.getId(), assetType);
                } else {
                    oldAsset = assetValidService.getUniqueAsset(asset, assetService, assetType);
                }
                if (oldAsset == null) {
                    String assetLabel = getAssetLabelIds(asset, "");
                    asset.setAssetLabel(assetLabel);
                    asset = addAssetByType(asset, assetType, false);
                    if (StringUtils.isNotBlank(taskId)) {
                        LiteFlowTaskAsset liteFlowTaskAsset = new LiteFlowTaskAsset();
                        liteFlowTaskAsset.setAssetId(asset.getId());
                        liteFlowTaskAsset.setLiteFlowTaskId(taskId);
                        liteFlowTaskAsset.setAssetType(assetType.getCode());
                        liteFlowTaskAssetMapper.insert(liteFlowTaskAsset);
                        logMessage.setMessage(Base64Encoder.encode("添加资产: " + asset + " 成功"));
                        elasticsearchTemplate.save(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, subTaskId, (JSONObject) JSONObject.toJSON(logMessage));
                    }
                    return asset;
                } else {
                    T assetDTO = assetService.convertDTO(oldAsset);
                    String assetLabel = getAssetLabelIds(asset, oldAsset.getAssetLabel());
                    // 新的属性复制到 DTO
                    BeanUtil.copyProperties(asset, assetDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                    assetDTO.setAssetLabel(assetLabel);
                    if (StringUtils.isNotBlank(taskId)) {
                        logMessage.setMessage(Base64Encoder.encode("更新资产: " + asset + " 成功"));
                        elasticsearchTemplate.save(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, subTaskId, (JSONObject) JSONObject.toJSON(logMessage));
                    }
                    return updateAssetByType(assetDTO, assetType, false);
                }
            }
        } catch (Exception e) {
            logMessage.setLevel("ERROR");
            logMessage.setMessage(Base64Encoder.encode("添加资产: " + asset + " 成功"));
            if (StringUtils.isNotBlank(logMessage.getTaskId())) {
                elasticsearchTemplate.save(Constants.ES_LOG_INDEX, Constants.ES_LOG_TYPE, subTaskId, (JSONObject) JSONObject.toJSON(logMessage));
            }
            log.error("添加或更新资产失败,资产类型:{},错误信息:{}", assetType, e.getMessage());
        }
        return null;
    }

    @Override
    public <T extends AssetBase> T addOrUpdate(T asset, AssetTypeEnums assetType) {
        return addOrUpdate(asset, assetType, "", "");
    }

    @Override
    public <T extends AssetBase> long getCountByDate(AssetTypeEnums assetType) {
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<T> queryWrapper = new QueryWrapper<>();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime startOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MIN);
                LocalDateTime endOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);
                queryWrapper.ge("create_time", startOfDay);
                queryWrapper.le("create_time", endOfDay);
                return assetService.count(queryWrapper);
            }
        } catch (Exception e) {
            log.error("按创建日期查询资产错误,资产类型:{},错误信息:{}", assetType, e.getMessage());
        }
        return 0;
    }

    @Override
    public <T extends AssetBase> long getAllCountByAssetType(AssetTypeEnums assetType) {
        try {
            IAssetService<T, T, T> assetService = (IAssetService<T, T, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                return assetService.count();
            }
        } catch (Exception e) {
            log.error("获取资产总数错误,资产类型:{},错误信息:{}", assetType, e.getMessage());
        }
        return 0;
    }


    private IAssetService getAssetServiceByType(AssetTypeEnums assetType) {
        String code = assetType.getCode();
        switch (code) {
            case "domain":
                return assetDomainService;
            case "sub_domain":
                return assetSubDomainService;
            case "ip":
                return assetIpService;
            case "port":
                return assetPortService;
            case "web":
                return assetWebService;
            case "vul":
                return assetVulService;
            case "api":
                return assetApiService;
            case "company":
                return assetCompanyService;
            default:
                log.error("没有找到:{} 对应的service", assetType);
                return null;
        }
    }

    @Override
    public Class<? extends AssetBase> getAssetClassByType(AssetTypeEnums assetType) {
        String code = assetType.getCode();
        switch (code) {
            case "domain":
                return AssetDomain.class;
            case "sub_domain":
                return AssetSubDomain.class;
            case "ip":
                return AssetIp.class;
            case "port":
                return AssetPort.class;
            case "web":
                return AssetWeb.class;
            case "vul":
                return AssetVul.class;
            case "api":
                return AssetApi.class;
            case "company":
                return AssetCompany.class;
            default:
                return null;
        }
    }

    @Override
    public void deleteAssetByQuery(String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        String assetType = jsonObject.getString("assetType");
        List<String> assetList = queryByAssetType(params, assetType);
        assetList.forEach(asset -> {
            JSONObject assetJson = JSONObject.parseObject(asset);
            String assetId = assetJson.getString("id");
            delByIdAndAssetType(assetId, AssetTypeEnums.fromCode(assetType));
        });
    }

    private <T extends AssetBase> String getAssetLabelIds(T asset, String oldAssetLabelsId) {
        String assetLabels = asset.getAssetLabel();
        if (StringUtils.isBlank(assetLabels) || assetLabels.equals(oldAssetLabelsId)) {
            return oldAssetLabelsId;
        }
        StringBuilder sb;
        if (StringUtils.isBlank(oldAssetLabelsId)) {
            sb = new StringBuilder();
        } else {
            sb = new StringBuilder(oldAssetLabelsId);
        }
        for (String label : assetLabels.split(",")) {
            AssetLabel assetLabel = assetLabelService.getAssetLabelByAssetName(label);
            if (assetLabel == null) {
                assetLabel = new AssetLabel();
                assetLabel.setLabelName(label);
                assetLabelService.save(assetLabel);
            }
            if (!sb.toString().contains(assetLabel.getId())) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(assetLabel.getId());
            }
        }
        return sb.toString();
    }


}
