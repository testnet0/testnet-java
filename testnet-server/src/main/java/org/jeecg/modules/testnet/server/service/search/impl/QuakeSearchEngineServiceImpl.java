package org.jeecg.modules.testnet.server.service.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.service.search.ISearchEngineService;
import org.jeecg.modules.testnet.server.vo.asset.AssetSearchVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import testnet.common.entity.HttpResponse;
import testnet.common.utils.HttpUtils;

import java.io.IOException;
import java.util.*;

@Service
public class QuakeSearchEngineServiceImpl implements ISearchEngineService {

    private static final Logger log = LoggerFactory.getLogger(QuakeSearchEngineServiceImpl.class);

    @Override
    public Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, AssetSearchEngine assetSearchEngine) {
        List<AssetSearchVO> assetSearchVOList = new ArrayList<>();
        String quakeServiceUrl = assetSearchEngine.getEngineHost();

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("query", assetSearchDTO.getKeyword());
            requestBody.put("start", assetSearchDTO.getPageNo());
            requestBody.put("size", assetSearchDTO.getPageSize());
            requestBody.put("latest", true); // 示例中默认为true，根据实际需求调整
            // 可选参数处理
            if (assetSearchDTO.getIgnoreCache() != null && assetSearchDTO.getIgnoreCache()) {
                requestBody.put("ignore_cache", true);
            }
            if (StringUtils.isNotBlank(assetSearchDTO.getStartTime())) {
                requestBody.put("start_time", assetSearchDTO.getStartTime());
            }
            if (StringUtils.isNotBlank(assetSearchDTO.getEndTime())) {
                requestBody.put("end_time", assetSearchDTO.getEndTime());
            }
            // 检查 shortcuts 是否为空或非空
            if (assetSearchDTO.getShortcuts() != null && !CollectionUtils.isEmpty(Arrays.asList(assetSearchDTO.getShortcuts().split(",")))) {
                List<String> shortcutsList = Arrays.asList(assetSearchDTO.getShortcuts().split(","));
                requestBody.put("shortcuts", shortcutsList);
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("X-QuakeToken", assetSearchEngine.getEngineToken());
            headers.put("Content-Type", "application/json");

            HttpResponse response = HttpUtils.post(quakeServiceUrl, headers, requestBody.toJSONString());
            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody();
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                log.info("Quake service search result: {}", responseBody);
                if ("0".equals(jsonObject.getString("code"))) {
                    JSONObject meta = jsonObject.getJSONObject("meta");
                    JSONObject pagination = meta.getJSONObject("pagination");
                    int total = pagination.getInteger("total");
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.size(); i++) {
                        JSONObject assetObj = data.getJSONObject(i);
                        AssetSearchVO assetSearchVO = new AssetSearchVO();

                        assetSearchVO.setIp(assetObj.getString("ip"));
                        assetSearchVO.setDomain(assetObj.getString("domain"));
                        assetSearchVO.setPort(assetObj.getInteger("port"));
                        assetSearchVO.setBaseProtocol(assetObj.getString("transport"));

                        JSONArray components = assetObj.getJSONArray("components");
                        if (components != null) {
                            JSONArray component = new JSONArray();
                            for (int j = 0; j < components.size(); j++) {
                                JSONObject componentObj = components.getJSONObject(j);
                                JSONObject tmp = new JSONObject();
                                if (StringUtils.isNotBlank(componentObj.getString("product_name_en"))) {
                                    tmp.put("name", componentObj.getString("product_name_en"));
                                } else {
                                    tmp.put("name", componentObj.getString("product_name_cn"));
                                }
                                tmp.put("version", componentObj.getString("version"));
                                component.add(tmp);
                            }
                            assetSearchVO.setComponent(component.toJSONString());
                        }

                        JSONObject location = assetObj.getJSONObject("location");
                        if (location != null) {
                            assetSearchVO.setCountry(location.getString("country_cn"));
                            assetSearchVO.setRegion(location.getString("province_cn"));
                            assetSearchVO.setCity(location.getString("city_cn"));
                        }

                        JSONObject service = assetObj.getJSONObject("service");
                        if (service != null) {
                            assetSearchVO.setBanner(service.getString("response"));

                            String protocol = service.getString("name");
                            if ("http/ssl".equals(protocol)) {
                                protocol = "https";
                            }
                            assetSearchVO.setProtocol(protocol);

                            JSONObject http = service.getJSONObject("http");
                            if (http != null) {
                                String host = http.getString("host");
                                assetSearchVO.setUrl(protocol + "://" + host + ":" + assetObj.getInteger("port"));
                                assetSearchVO.setStatusCode(http.getInteger("status_code"));
                                assetSearchVO.setHeader(http.getString("response_headers"));
                                assetSearchVO.setTitle(http.getString("title"));

                                JSONObject icp = http.getJSONObject("icp");
                                if (icp != null) {
                                    JSONObject mainLicence = icp.getJSONObject("main_licence");
                                    if (mainLicence != null) {
                                        assetSearchVO.setIcpNumber(mainLicence.getString("licence"));
                                        assetSearchVO.setCompany(mainLicence.getString("unit"));
                                    }
                                }

                                assetSearchVO.setResponseHash(http.getString("response_hash"));

                                JSONObject favicon = http.getJSONObject("favicon");
                                if (favicon != null) {
                                    assetSearchVO.setIconHash(favicon.getString("hash"));
                                    assetSearchVO.setIconUrl(favicon.getString("s3_url"));
                                }
                            }
                        }

                        assetSearchVOList.add(assetSearchVO);
                    }

                    IPage<AssetSearchVO> page = new Page<>(assetSearchDTO.getPageNo(), assetSearchDTO.getPageSize(), total);
                    page.setRecords(assetSearchVOList);
                    return Result.OK("查询成功", page);
                } else {
                    return Result.error(403, "Quake service search error: " + jsonObject.getString("message"));
                }
            } else if (response.getStatusCode() == 401) {
                return Result.error(403, "查询失败，请检查Key是否配置正确！");
            } else {
                return Result.error(500, "查询失败，服务器返回状态码：" + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Quake service search error: {}", e.getMessage());
            return Result.error("Quake service search error: " + e.getMessage());
        }
    }
}
