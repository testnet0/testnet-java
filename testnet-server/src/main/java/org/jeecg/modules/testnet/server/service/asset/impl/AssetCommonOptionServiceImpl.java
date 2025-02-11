/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-06-27
 **/
package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.boot.starter.lock.client.RedissonLockClient;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.PmsUtil;
import org.jeecg.common.util.ReflectHelper;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.dto.AssetPortDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetVulDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.*;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTaskAsset;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskAssetMapper;
import org.jeecg.modules.testnet.server.service.asset.*;
import org.jeecg.modules.testnet.server.service.log.ILogService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private AssetApiServiceImpl assetApiService;
    @Resource
    private IAssetValidService assetValidService;

    @Resource
    private IAssetLabelService assetLabelService;


    @Resource
    private LiteFlowTaskAssetMapper liteFlowTaskAssetMapper;

    @Resource
    private RedissonLockClient redissonLockClient;

    @Resource
    private ILogService logService;

    @Resource
    private IProjectService projectService;


    @Override
    public <D extends AssetBase> List<String> queryByAssetType(String params, String assetType) {
        List<String> assetsList = new ArrayList<>();
        List<? extends AssetBase> assets = queryAssetDOListByQueryAndAssetType(params, assetType);
        IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(AssetTypeEnums.fromCode(assetType));
        if (assetService != null && assets != null && !assets.isEmpty()) {
            assets.forEach(o -> {
                assetsList.add(JSONObject.toJSONString(assetService.convertDTO((D) o)));
            });
        }
        return assetsList;
    }

    @Override
    public <D extends AssetBase> List<? extends AssetBase> queryAssetDOListByQueryAndAssetType(String params, String assetType) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        JSONObject query = jsonObject.getJSONObject("queryParam");
        String asset = jsonObject.getString("queryObject");
        List<? extends AssetBase> assetsList = new ArrayList<>();
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
            Class<D> assetClass = (Class<D>) getAssetClassByType(AssetTypeEnums.fromCode(assetType));
            IAssetService<D, D, D> assetService = (IAssetService<D, D, D>) getAssetServiceByType(AssetTypeEnums.fromCode(assetType));
            if (assetClass != null && assetService != null) {
                D assetInstance = JSONObject.parseObject(asset, assetClass);
                QueryWrapper<D> queryWrapper;
                if (assetInstance != null) {
                    queryWrapper = QueryGenerator.initQueryWrapper(assetInstance, queryMap);
                } else {
                    queryWrapper = QueryGenerator.initQueryWrapper(assetClass.getDeclaredConstructor().newInstance(), queryMap);
                }
                return assetService.list(queryWrapper);
            }
        } catch (Exception e) {
            log.error("查询资产类型错误:{}", e.getMessage());
        }
        return assetsList;
    }

    @Override
    public <D extends AssetBase> void delByIdAndAssetType(String assetId, AssetTypeEnums assetType) {
        try {
            IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ?, ?>) getAssetServiceByType(assetType);
            if (assetService != null) {
                assetService.delRelation(Arrays.asList(assetId.split(",")));
                assetService.removeById(assetId);
            }
        } catch (Exception e) {
            log.error("删除资产类型：{} 错误:{}", assetType, e.getMessage());
        }
    }

    @Override
    public <D extends AssetBase> void delAssetByProjectId(String projectId, AssetTypeEnums assetType) {
        try {
            IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<D> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id", projectId);
                assetService.remove(queryWrapper);
            }
        } catch (Exception e) {
            log.error("删除项目失败，资产类型：{} 错误:{}", assetType, e.getMessage());
        }
    }

    @Override
    public <D extends AssetBase> long getCountByProjectId(String projectId, AssetTypeEnums assetType) {
        try {
            IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<D> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id", projectId);
                return assetService.count(queryWrapper);
            }
        } catch (Exception e) {
            log.error("获取项目关联资产数量失败，资产类型：{} 错误:{}", assetType, e.getMessage());
        }
        return 0;
    }

    @Override
    public <D extends AssetBase, V extends AssetBase> IPage<V> page(D asset, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap, AssetTypeEnums assetType) {
        try {
            IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<D> queryWrapper = QueryGenerator.initQueryWrapper(asset, parameterMap);
                IPage<D> pageInfo = new Page<>(pageNo, pageSize);
                IPage<D> pageData = assetService.page(pageInfo, queryWrapper, parameterMap);
                IPage<V> pageVO = new Page<>();
                BeanUtil.copyProperties(pageData, pageVO);
                List<V> assetsList = new ArrayList<>();
                if (pageData.getRecords() != null) {
                    pageData.getRecords().forEach(record -> {
                        assetsList.add((V) assetService.convertVO(record));
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
    public <T extends AssetBase> Result<? extends AssetBase> addAssetByType(T asset, AssetTypeEnums assetType) {
        return addAssetByType(asset, assetType, true);
    }


    @Override
    public Result<? extends AssetBase> getAssetDOByIdAndAssetType(String id, AssetTypeEnums assetType) {
        IAssetService<? extends AssetBase, ? extends AssetBase, ? extends AssetBase> assetService = getAssetServiceByType(assetType);
        if (assetService != null) {
            return Result.ok(assetService.getById(id));
        } else {
            return Result.error("未找到该资产");
        }
    }


    @Override
    public <D extends AssetBase> Result<? extends AssetBase> getByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType) {
        IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
        if (assetService != null) {
            QueryWrapper<D> queryWrapper = new QueryWrapper<>();
            fieldAndValue.forEach(queryWrapper::eq);
            return Result.ok(assetService.convertDTO(assetService.getOne(queryWrapper)));
        } else {
            return Result.error("未找到该资产类型的服务" + assetType);
        }
    }

    @Override
    public <D extends AssetBase, T extends AssetBase> Result<List<? extends AssetBase>> listByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType) {
        IAssetService<D, ?, T> assetService = (IAssetService<D, ?, T>) getAssetServiceByType(assetType);
        if (assetService != null) {
            QueryWrapper<D> queryWrapper = new QueryWrapper<>();
            fieldAndValue.forEach(queryWrapper::eq);
            List<D> assets = assetService.list(queryWrapper);
            List<T> assetsList = assets.stream().map(assetService::convertDTO).collect(Collectors.toList());
            return Result.ok(assetsList);
        }
        return Result.error("未找到该资产类型的服务" + assetType);
    }

    @Override
    public <D extends AssetBase, T extends AssetBase> Result<? extends AssetBase> getDTOByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType) {
        IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
        if (assetService != null) {
            QueryWrapper<D> queryWrapper = new QueryWrapper<>();
            fieldAndValue.forEach(queryWrapper::eq);
            D asset = assetService.getOne(queryWrapper);
            if (asset != null) {
                return Result.ok(assetService.convertDTO(asset));
            }
        }
        return Result.error("未找到该资产");
    }

    @Override
    public <D extends AssetBase, T extends AssetBase> Result<? extends AssetBase> addAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate) {
        Result<? extends AssetBase> result = assetValidService.isValid(asset, assetType);
        if (!result.isSuccess()) {
            return result;
        }
        if (checkDuplicate && getUniqueAsset(asset, assetType) != null) {
            log.error("添加:{}, 资产：{}错误，存在重复值！", assetType, asset);
            return Result.error("添加资产错误，存在重复值！");
        }
        try {
            IAssetService<D, ? extends AssetBase, T> assetService = (IAssetService<D, ? extends AssetBase, T>) getAssetServiceByType(assetType);
            Class<T> assetClass = (Class<T>) getAssetDTOClassByType(assetType);
            if (assetClass != null && assetService != null) {
                if (assetService.addAssetByType(asset)) {
                    return Result.OK(asset);
                } else {
                    return Result.error("添加资产错误");
                }
            } else {
                log.error("添加:{}, 资产：{} 发生错误，未找到对应类型！", assetType, asset);
                return Result.error("添加资产错误，未找到对应类型！");
            }
        } catch (Exception e) {
            log.error("添加资产:{} 发生错误:{}", asset, e.getMessage());
            return Result.error("添加" + asset + "发生错误:" + e.getMessage());
        }
    }

    @Override
    public <T extends AssetBase> Result<? extends AssetBase> updateAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate) {
        Result<T> result = assetValidService.isValid(asset, assetType);
        if (!result.isSuccess()) {
            return result;
        }
        if (checkDuplicate && getUniqueAsset(asset, assetType) != null) {
            log.error("更新:{}, 资产：{} 发生错误，存在重复值！", assetType, asset);
            return Result.error("更新资产错误，存在重复值！");
        }
        try {
            IAssetService<?, ?, T> assetService = (IAssetService<?, ?, T>) getAssetServiceByType(assetType);
            Class<T> assetClass = (Class<T>) getAssetClassByType(assetType);
            if (assetClass != null && assetService != null) {
                if (assetService.updateAssetByType(asset)) {
                    return Result.OK(asset);
                } else {
                    return Result.error("更新资产错误");
                }
            } else {
                log.error("更新:{}, 资产：{} 发生错误，未找到对应类型！", assetType, asset);
                return Result.error("更新资产错误，未找到对应类型！");
            }
        } catch (Exception e) {
            log.error("更新:{}, 资产：{} 发生错误：{}", assetType, asset, e.getMessage());
            return Result.error("更新" + assetType + ", 资产：" + asset + "发生错误:" + e.getMessage());
        }
    }

    @Override
    public <T extends AssetBase> Result<? extends AssetBase> updateAssetByType(T asset, AssetTypeEnums assetType) {
        return updateAssetByType(asset, assetType, true);
    }

    @Override
    public <T extends AssetBase, D extends AssetBase> Result<? extends AssetBase> addOrUpdate(T asset, AssetTypeEnums assetType, String taskId, String subTaskId) {

        try {
            IAssetService<D, ?, T> assetService = (IAssetService<D, ?, T>) getAssetServiceByType(assetType);
            if (assetService != null) {
                Result<T> assetVaildResult = assetValidService.isValid(asset, assetType);
                if (!assetVaildResult.isSuccess()) {
                    return assetVaildResult;
                }
                D oldAsset;
                // 处理项目
                Project project = projectService.getByProjectIdOrName(asset.getProjectId());
                if (project == null) {
                    project = new Project();
                    project.setProjectName(asset.getProjectId());
                    projectService.save(project);
                }
                asset.setProjectId(project.getId());
                // 处理资产标签
                if (StringUtils.isNotBlank(asset.getAssetLabel())) {
                    List<String> idList = new ArrayList<>();
                    for (String s : asset.getAssetLabel().split(",")) {
                        if (StringUtils.isBlank(s)) {
                            continue;
                        }
                        AssetLabel assetLabel = assetLabelService.getByLabelIdOrName(s);
                        if (assetLabel == null) {
                            assetLabel = new AssetLabel();
                            assetLabel.setLabelName(s);
                            assetLabelService.save(assetLabel);
                        }
                        idList.add(assetLabel.getId());
                    }
                    asset.setAssetLabel(String.join(",", idList));
                }
                // 获得锁
                String shaKey = assetValidService.getShaKey(asset, assetType);
                if (redissonLockClient.tryLock(shaKey, 10, 10)) {
                    if (StringUtils.isNotBlank(asset.getId())) {
                        Result<? extends AssetBase> assetResult = getAssetDOByIdAndAssetType(asset.getId(), assetType);
                        if (assetResult.isSuccess() && assetResult.getResult() != null) {
                            oldAsset = (D) assetResult.getResult();
                        } else {
                            return Result.error("未找到对应数据");
                        }
                    } else {
                        oldAsset = getUniqueAsset(asset, assetType);
                    }
                    if (oldAsset == null) {
                        Result<? extends AssetBase> addAssetResult;
                        // 如果来源是任务 那么记录到日志
                        if (StringUtils.isNotBlank(taskId)) {
                            String assetLabel = getAssetLabelIds(asset, "");
                            asset.setAssetLabel(assetLabel);
                            LiteFlowTaskAsset liteFlowTaskAsset = new LiteFlowTaskAsset();
                            liteFlowTaskAsset.setAssetId(asset.getId());
                            liteFlowTaskAsset.setLiteFlowTaskId(taskId);
                            liteFlowTaskAsset.setAssetType(assetType.getCode());
                            liteFlowTaskAssetMapper.insert(liteFlowTaskAsset);
                            addAssetResult = addAssetByType(asset, assetType, false);
                            if (addAssetResult.isSuccess()) {
                                logService.addINFOLog("server", "添加资产: " + asset + " 成功", subTaskId);
                            } else {
                                logService.addERRORLog("server", "添加资产: " + asset + " 失败,原因：" + addAssetResult.getMessage(), subTaskId);
                            }
                            redissonLockClient.unlock(shaKey);
                            return addAssetResult;
                        } else {
                            addAssetResult = addAssetByType(asset, assetType, false);
                        }
                        redissonLockClient.unlock(shaKey);
                        return addAssetResult;
                    } else {
                        String assetLabel = getAssetLabelIds(asset, oldAsset.getAssetLabel());
                        T assetDTO = assetService.convertDTO(oldAsset);
                        // 新的属性复制到 DTO
                        BeanUtil.copyProperties(asset, assetDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        assetDTO.setAssetLabel(assetLabel);
                        logService.addINFOLog("server", "更新资产:" + asset + " 成功", subTaskId);
                        Result<? extends AssetBase> updateAssetResult = updateAssetByType(assetDTO, assetType, false);
                        redissonLockClient.unlock(shaKey);
                        return updateAssetResult;
                    }
                } else {
                    log.error("获取资产锁失败,资产:{}", asset);
                }
            }
        } catch (Exception e) {
            logService.addERRORLog("server", "更新资产:" + asset + " 失败", subTaskId);
            log.error("添加或更新资产失败,资产类型:{},错误信息:{}", assetType, e.getMessage());
        }
        return null;
    }

    @Override
    public <T extends AssetBase> Result<? extends AssetBase> addOrUpdate(T asset, AssetTypeEnums assetType) {
        return addOrUpdate(asset, assetType, "", "");
    }

    @Override
    public <D extends AssetBase> long getCountByDate(AssetTypeEnums assetType) {
        try {
            IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
            if (assetService != null) {
                QueryWrapper<D> queryWrapper = new QueryWrapper<>();
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
    public <D extends AssetBase> long getAllCountByAssetType(AssetTypeEnums assetType) {
        try {
            IAssetService<D, ? extends AssetBase, ? extends AssetBase> assetService = (IAssetService<D, ? extends AssetBase, ? extends AssetBase>) getAssetServiceByType(assetType);
            if (assetService != null) {
                return assetService.count();
            }
        } catch (Exception e) {
            log.error("获取资产总数错误,资产类型:{},错误信息:{}", assetType, e.getMessage());
        }
        return 0;
    }

    private IAssetService<? extends AssetBase, ? extends AssetBase, ? extends AssetBase> getAssetServiceByType(AssetTypeEnums assetType) {
        String code = assetType.getCode();
        switch (code) {
            case "domain":
                return assetDomainService; // 强制类型转换
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
    public Class<? extends AssetBase> getAssetDTOClassByType(AssetTypeEnums assetType) {
        String code = assetType.getCode();
        switch (code) {
            case "domain":
                return AssetDomain.class;
            case "sub_domain":
                return AssetSubDomainIpsDTO.class;
            case "ip":
                return AssetIpDTO.class;
            case "port":
                return AssetPortDTO.class;
            case "web":
                return AssetWebDTO.class;
            case "vul":
                return AssetVulDTO.class;
            case "api":
                return AssetApiDTO.class;
            case "company":
                return AssetCompany.class;
            default:
                return null;
        }
    }

    @Override
    @Async
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

    @Override
    public <T extends AssetBase> Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<? extends AssetBase> clazz, AssetTypeEnums assetType) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<T> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                return batchAdd(list, assetType);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return Result.error("文件导入失败！");
    }

    @Override
    public <T extends AssetBase> Result<?> batchAdd(List<T> list, AssetTypeEnums assetType) {
        List<String> errorMessage = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            Result<?> result = addOrUpdate(t, assetType);
            if (!result.isSuccess()) {
                errorMessage.add("第" + (i + 1) + "行数据处理失败,原因:" + result.getMessage());
            }
        }
        log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
        if (errorMessage.isEmpty()) {
            return Result.ok("共" + list.size() + "行数据全部成功！");
        } else {
            JSONObject result = new JSONObject(5);
            int totalCount = list.size();
            int errorLines = errorMessage.size();
            int successLines = totalCount - errorLines;
            result.put("totalCount", totalCount);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            result.put("msg", "总行数：" + totalCount + "，成功行数：" + successLines + "，错误行数：" + errorLines);
            result.put("errorMessage", errorMessage);
            String fileUrl = PmsUtil.saveErrorTxtByList(errorMessage);
            int lastIndex = fileUrl.lastIndexOf(File.separator);
            String fileName = fileUrl.substring(lastIndex + 1);
            result.put("fileUrl", "/sys/common/static/" + fileUrl);
            result.put("fileName", fileName);
            Result<JSONObject> res = Result.ok(result);
            res.setCode(201);
            res.setMessage("批量导入成功，但有错误。");
            return res;
        }
    }

    @Override
    @Async
    public <D extends AssetBase> void handleChangeLabels(String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        String assetType = jsonObject.getString("assetType");
        String type = jsonObject.getString("type");
        String labelIds = jsonObject.getString("labelIds");
        if (jsonObject.getJSONArray("data") != null && !jsonObject.getJSONArray("data").isEmpty()) {
            jsonObject.getJSONArray("data").forEach(assetId -> {
                Result<? extends AssetBase> result = getAssetDOByIdAndAssetType(assetId.toString(), AssetTypeEnums.fromCode(assetType));
                if (result.isSuccess() && result.getResult() != null) {
                    changeLabelsByAssetType(type, labelIds, result.getResult(), AssetTypeEnums.fromCode(assetType));
                }
            });
        } else {
            List<? extends AssetBase> assetList = queryAssetDOListByQueryAndAssetType(params, assetType);
            assetList.forEach(asset -> {
                changeLabelsByAssetType(type, labelIds, asset, AssetTypeEnums.fromCode(assetType));
            });
        }
    }

    @Override
    public void handleChangeVulStatus(String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        String vulStatus = jsonObject.getString("vulStatus");
        if (jsonObject.getJSONArray("data") != null && !jsonObject.getJSONArray("data").isEmpty()) {
            jsonObject.getJSONArray("data").forEach(vulId -> {
                assetVulService.changeVulStatus(vulId.toString(), vulStatus);
            });
        } else {
            List<? extends AssetBase> assetList = queryAssetDOListByQueryAndAssetType(params, AssetTypeEnums.VUL.getCode());
            assetList.forEach(asset -> {
                assetVulService.changeVulStatus(asset.getId(), vulStatus);
            });
        }
    }

    private <D extends AssetBase> void changeLabelsByAssetType(String type, String labelIds, D asset, AssetTypeEnums assetType) {
        Set<String> existingLabels = new HashSet<>(Arrays.asList(asset.getAssetLabel() == null ? new String[]{} : asset.getAssetLabel().split(",")));
        Set<String> labelsToModify = labelIds == null ? new HashSet<>() : new HashSet<>(Arrays.asList(labelIds.split(",")));

        switch (type) {
            case "add":
                existingLabels.addAll(labelsToModify);
                break;

            case "remove":
                existingLabels.removeAll(labelsToModify);
                break;

            case "update":
                existingLabels = labelsToModify;
                break;

            case "clear":
                existingLabels.clear();
                break;

            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }

        // 构建新的标签字符串
        StringBuilder newLabels = new StringBuilder();
        for (String label : existingLabels) {
            if (newLabels.length() > 0) {
                newLabels.append(",");
            }
            newLabels.append(label);
        }

        asset.setAssetLabel(newLabels.toString());

        IAssetService assetService = getAssetServiceByType(assetType);
        if (assetService != null) {
            if (redissonLockClient.tryLock(assetType.getCode() + asset.getId(), 10, 10)) {
                assetService.updateById(asset);
            }
            redissonLockClient.unlock(assetType.getCode() + asset.getId());
        }
    }


    private <T extends AssetBase> String getAssetLabelIds(T asset, String oldAssetLabelsId) {
        String assetLabelIds = asset.getAssetLabel();
        if (StringUtils.isBlank(assetLabelIds) || assetLabelIds.equals(oldAssetLabelsId)) {
            return oldAssetLabelsId;
        }
        StringBuilder sb;
        if (StringUtils.isBlank(oldAssetLabelsId)) {
            sb = new StringBuilder();
        } else {
            sb = new StringBuilder(oldAssetLabelsId);
        }
        for (String labelId : assetLabelIds.split(",")) {
            if (!sb.toString().contains(labelId)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(labelId);
            }
        }
        return sb.toString();
    }


    @Override
    public <T extends AssetBase, D extends AssetBase> D getUniqueAsset(T asset, AssetTypeEnums assetType) {
        QueryWrapper<D> queryWrapper = new QueryWrapper<>();
        if (org.apache.commons.lang.StringUtils.isNotBlank(asset.getId())) {
            queryWrapper.ne("id", asset.getId());
        }
        Map<String, String> duplicateCheckFieldNames = assetValidService.getUniqueCheckFieldName(assetType);
        duplicateCheckFieldNames.forEach((k, v) -> {
            Object fieldValue = ReflectHelper.getFieldVal(v, asset);
            if (fieldValue != null) {
                queryWrapper.eq(k, fieldValue);
            } else {
                queryWrapper.eq(k, "");
            }
        });
        IAssetService<D, D, D> assetService = (IAssetService<D, D, D>) getAssetServiceByType(assetType);
        return assetService.getOne(queryWrapper);
    }
}
