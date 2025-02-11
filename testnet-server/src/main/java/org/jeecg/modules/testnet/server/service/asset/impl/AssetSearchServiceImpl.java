/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-03
 **/
package org.jeecg.modules.testnet.server.service.asset.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.net.InternetDomainName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.*;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.mapper.liteflow.LiteFlowTaskMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchEngineService;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.search.impl.*;
import org.jeecg.modules.testnet.server.vo.AssetSearchVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.enums.LiteFlowStatusEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class AssetSearchServiceImpl implements IAssetSearchService {

    @Resource
    private IAssetSearchEngineService assetSearchEngineService;

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private LiteFlowTaskMapper liteFlowTaskMapper;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;


    @Resource
    private HunterSearchEngineServiceImpl hunterSearchEngineService;
    @Resource
    private FofaSearchEngineServiceImpl fofaSearchEngineService;
    @Resource
    private QuakeSearchEngineServiceImpl quakeSearchEngineService;
    @Resource
    private ShodanSearchEngineServiceImpl shodanSearchEngineService;
    @Resource
    private ZeroZoneSearchEngineServiceImpl zeroZoneSearchEngineService;


    @Override
    public Result<IPage<AssetSearchVO>> list(AssetSearchDTO assetSearchDTO) {
        if (StringUtils.isEmpty(assetSearchDTO.getKeyword())) {
            IPage<AssetSearchVO> page = new Page<>();
            page.setPages(10);
            page.setCurrent(1);
            page.setTotal(1);
            return Result.OK(page);
        } else {
            AssetSearchEngine assetSearchEngine = assetSearchEngineService.getKey(assetSearchDTO.getEngine());
            switch (assetSearchDTO.getEngine()) {
                case "hunter":
                    return hunterSearchEngineService.search(assetSearchDTO, assetSearchEngine);
                case "fofa":
                    return fofaSearchEngineService.search(assetSearchDTO, assetSearchEngine);
                case "quake":
                    return quakeSearchEngineService.search(assetSearchDTO, assetSearchEngine);
                case "shodan":
                    return shodanSearchEngineService.search(assetSearchDTO, assetSearchEngine);
                case "0zone":
                    return zeroZoneSearchEngineService.search(assetSearchDTO, assetSearchEngine);
                default:
                    return Result.error("查询失败！不支持的类型");
            }
        }
    }


    @Override
    @Async
    public void importAsset(AssetSearchImportDTO assetSearchImportDTO, String taskId, String subTaskId) {
        if (assetSearchImportDTO.getData() != null && !assetSearchImportDTO.getData().isEmpty()) {
            assetSearchImportDTO.getData().forEach(assetSearchVO -> {
                // IP必须存在
                if (StringUtils.isNotBlank(assetSearchVO.getIp())) {
                    AssetIpDTO assetIpDTO = new AssetIpDTO();
                    Result<? extends AssetBase> assetIpResult;
                    Result<? extends AssetBase> assetDomainResult;
                    Result<? extends AssetBase> assetSubDomainResult = new Result<>();
                    Result<? extends AssetBase> assetCompanyResult = new Result<>();

                    AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
                    if (StringUtils.isNotBlank(assetSearchVO.getDomain()) && StringUtils.isNotBlank(getTopDomain(assetSearchVO.getDomain()))) {
                        AssetDomain assetDomain = new AssetDomain();
                        if (StringUtils.isNotBlank(assetSearchVO.getCompany())) {
                            AssetCompany assetCompany = new AssetCompany();
                            assetCompany.setCompanyName(assetSearchVO.getCompany());
                            assetCompany.setSource(assetSearchImportDTO.getEngine());
                            assetCompany.setProjectId(assetSearchImportDTO.getProjectId());
                            assetCompanyResult = assetCommonOptionService.addOrUpdate(assetCompany, AssetTypeEnums.COMPANY, taskId, subTaskId);
                        }
                        assetDomain.setDomain(getTopDomain(assetSearchVO.getDomain()));
                        assetDomain.setSource(assetSearchImportDTO.getEngine());
                        assetDomain.setProjectId(assetSearchImportDTO.getProjectId());
                        assetDomain.setIcpNumber(assetSearchVO.getIcpNumber());
                        if (assetCompanyResult.isSuccess() && assetCompanyResult.getResult() != null) {
                            assetDomain.setCompanyId(assetCompanyResult.getResult().getId());
                        }
                        assetDomainResult = assetCommonOptionService.addOrUpdate(assetDomain, AssetTypeEnums.DOMAIN, taskId, subTaskId);
                        if (!assetDomainResult.isSuccess() || assetDomainResult.getResult() == null) {
                            log.error("添加域名失败！");
                            return;
                        }
                        assetSubDomainIpsDTO.setDomainId(assetDomainResult.getResult().getId());
                        assetSubDomainIpsDTO.setSubDomain(assetSearchVO.getDomain());
                        assetSubDomainIpsDTO.setIps(assetSearchVO.getIp());
                        assetSubDomainIpsDTO.setSource(assetSearchImportDTO.getEngine());

                        assetSubDomainIpsDTO.setProjectId(assetSearchImportDTO.getProjectId());
                        assetSubDomainResult = assetCommonOptionService.addOrUpdate(assetSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, taskId, subTaskId);
                        if (!assetSubDomainResult.isSuccess() || assetSubDomainResult.getResult() == null) {
                            log.error("添加子域名失败！");
                            return;
                        }
                        Map<String, String> map = new HashMap<>();
                        map.put("ip", assetSearchVO.getIp());
                        map.put("project_id", assetSearchImportDTO.getProjectId());
                        assetIpResult = assetCommonOptionService.getDTOByFieldAndAssetType(map, AssetTypeEnums.IP);
                    } else {
                        assetIpDTO.setIp(assetSearchVO.getIp());
                        assetIpDTO.setIsp(assetSearchVO.getIsp());
                        assetIpDTO.setCountry(assetSearchVO.getCountry());
                        assetIpDTO.setProvince(assetSearchVO.getProvince());
                        assetIpDTO.setProjectId(assetSearchImportDTO.getProjectId());
                        assetIpDTO.setCity(assetSearchVO.getCity());
                        assetIpDTO.setSource(assetSearchImportDTO.getEngine());
                        assetIpDTO.setSubDomains("");
                        assetIpResult = assetCommonOptionService.addOrUpdate(assetIpDTO, AssetTypeEnums.IP, taskId, subTaskId);
                        if (!assetIpResult.isSuccess() || assetIpResult.getResult() == null) {
                            log.error("添加IP失败！");
                            return;
                        }
                    }

                    if (assetSearchVO.getPort() > 0) {
                        AssetPortDTO assetPortDTO = new AssetPortDTO();
                        assetPortDTO.setIp(assetIpResult.getResult().getId());
                        assetPortDTO.setPort(assetSearchVO.getPort());
                        assetPortDTO.setProjectId(assetSearchImportDTO.getProjectId());
                        assetPortDTO.setSource(assetSearchImportDTO.getEngine());
                        assetPortDTO.setIsWeb(assetSearchVO.getIsWeb());
                        assetPortDTO.setIsOpen("Y");
                        assetPortDTO.setProtocol(assetSearchVO.getBaseProtocol());
                        Result<? extends AssetBase> assetPortResult = assetCommonOptionService.addOrUpdate(assetPortDTO, AssetTypeEnums.PORT, taskId, subTaskId);
                        if (assetPortResult.isSuccess() && assetPortResult.getResult() != null) {
                            if (StringUtils.isNotBlank(assetSearchVO.getUrl())) {
                                AssetWebDTO assetWeb = new AssetWebDTO();
                                assetWeb.setPortId(assetPortResult.getResult().getId());
                                if (assetSubDomainResult.isSuccess() && assetSubDomainResult.getResult() != null) {
                                    assetWeb.setDomain(assetSubDomainResult.getResult().getId());
                                }
                                assetWeb.setSource(assetSearchImportDTO.getEngine());
                                assetWeb.setProjectId(assetSearchImportDTO.getProjectId());
                                assetWeb.setStatusCode(assetSearchVO.getStatusCode());
                                assetWeb.setHttpSchema(assetSearchVO.getProtocol());
                                assetWeb.setWebTitle(assetSearchVO.getTitle().length() > 1000 ? assetSearchVO.getTitle().substring(0, 1000) : assetSearchVO.getTitle());
                                assetWeb.setWebUrl(assetSearchVO.getUrl());
                                assetWeb.setIconUrl(assetSearchVO.getIconUrl());
                                assetWeb.setWebHeader(assetSearchVO.getBanner());
                                assetWeb.setTech(assetSearchVO.getComponent());
                                assetWeb.setBodyMd5(assetSearchVO.getResponseHash());
                                assetWeb.setBody(assetSearchVO.getBody());
                                assetCommonOptionService.addOrUpdate(assetWeb, AssetTypeEnums.WEB, taskId, subTaskId);
                            }
                        }
                    } else {
                        log.error("IP导入失败！：{}", assetSearchVO);
                    }

                } else {
                    log.error("导入失败，IP为空！:{}", assetSearchVO);
                }
            });
        } else {
            importBatch(assetSearchImportDTO, taskId, subTaskId);
        }
    }

    @Override
    public void executeAgain(LiteFlowTask liteFlowTask) {
        AssetSearchImportDTO assetSearchImportDTO = JSONObject.parseObject(liteFlowTask.getSearchParam(), AssetSearchImportDTO.class);
        AssetSearchDTO params = assetSearchImportDTO.getParams();
        Result<IPage<AssetSearchVO>> result = list(params);
        IPage<AssetSearchVO> page = result.getResult();
        if (page != null && page.getRecords() != null && !page.getRecords().isEmpty()) {
            int totalPages = (int) ((page.getTotal() + params.getPageSize() - 1) / params.getPageSize());
            liteFlowTask.setVersion(liteFlowTask.getVersion() + 1);
            LiteFlowSubTask liteFlowSubTask = new LiteFlowSubTask();
            liteFlowSubTask.setTaskId(liteFlowTask.getId());
            liteFlowSubTask.setVersion(liteFlowTask.getVersion());
            liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.SUCCEED.name());
            assetSearchImportDTO.setData(page.getRecords());
            liteFlowSubTaskService.save(liteFlowSubTask);
            importAsset(assetSearchImportDTO, liteFlowTask.getId(), liteFlowSubTask.getId());
            if (totalPages > 1) {
                liteFlowTask.setUnFinishedChain(totalPages - 1);
            }
            liteFlowTaskMapper.updateById(liteFlowTask);
            List<LiteFlowSubTask> subTaskList = getLiteFlowSubTasks(totalPages, liteFlowTask, liteFlowTask.getVersion());
            liteFlowSubTaskService.saveBatch(subTaskList);
        }

    }


    private void importBatch(AssetSearchImportDTO assetSearchImportDTO, String taskId, String subTaskId) {
        AssetSearchDTO params = assetSearchImportDTO.getParams();
        Result<IPage<AssetSearchVO>> result = list(params);
        IPage<AssetSearchVO> page = result.getResult();
        if (page != null && page.getRecords() != null && !page.getRecords().isEmpty()) {
            int totalPages = (int) ((page.getTotal() + params.getPageSize() - 1) / params.getPageSize());
            LiteFlowTask liteFlowTask = new LiteFlowTask();
            liteFlowTask.setTaskName(params.getEngine() + "批量导入任务");
            liteFlowTask.setSearchParam(JSONObject.toJSONString(assetSearchImportDTO));
            liteFlowTask.setChainId("1820100181514387457");
            liteFlowTask.setRouter("0");
            liteFlowTask.setVersion(1);
            if (totalPages > 1) {
                liteFlowTask.setUnFinishedChain(totalPages - 1);
            }
            liteFlowTaskMapper.insert(liteFlowTask);

            liteFlowTask.setVersion(liteFlowTask.getVersion() + 1);
            LiteFlowSubTask liteFlowSubTask = new LiteFlowSubTask();
            liteFlowSubTask.setTaskId(liteFlowTask.getId());
            liteFlowSubTask.setVersion(liteFlowTask.getVersion());
            liteFlowSubTask.setTaskStatus(LiteFlowStatusEnums.SUCCEED.name());
            assetSearchImportDTO.setData(page.getRecords());
            importAsset(assetSearchImportDTO, taskId, subTaskId);

            List<LiteFlowSubTask> subTaskList = getLiteFlowSubTasks(totalPages, liteFlowTask, 1);
            liteFlowSubTaskService.saveBatch(subTaskList);

        }
    }

    private List<LiteFlowSubTask> getLiteFlowSubTasks(int totalPages, LiteFlowTask liteFlowTask, Integer version) {
        List<LiteFlowSubTask> subTaskList = new ArrayList<>();
        for (int currentPage = 2; currentPage <= totalPages; currentPage++) {
            JSONObject param = new JSONObject();
            param.put("currentPage", currentPage);
            LiteFlowSubTask subTask = new LiteFlowSubTask();
            subTask.setTaskId(liteFlowTask.getId());
            subTask.setVersion(version);
            subTask.setTaskStatus(LiteFlowStatusEnums.PENDING.name());
            subTask.setSubTaskParam(param.toJSONString());
            subTaskList.add(subTask);
        }
        return subTaskList;
    }


    private String getTopDomain(String url) {
        String domain = url.replaceFirst("^(http://|https://|ftp://|www\\.)", "");

        try {
            InternetDomainName internetDomainName = InternetDomainName.from(domain);
            InternetDomainName topPrivateDomain = internetDomainName.topPrivateDomain();
            return topPrivateDomain.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
