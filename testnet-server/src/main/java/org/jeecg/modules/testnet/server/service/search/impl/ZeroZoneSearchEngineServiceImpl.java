package org.jeecg.modules.testnet.server.service.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.service.search.ISearchEngineService;
import org.jeecg.modules.testnet.server.vo.asset.AssetSearchVO;
import org.springframework.stereotype.Service;
import testnet.common.entity.HttpResponse;
import testnet.common.utils.HttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ZeroZoneSearchEngineServiceImpl implements ISearchEngineService {
    @Override
    public Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, AssetSearchEngine assetSearchEngine) {
        List<AssetSearchVO> assetSearchVOList = new ArrayList<>();
        String baseUrl = assetSearchEngine.getEngineHost();
        try {

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            // 发起GET请求
            JSONObject body = new JSONObject();
            body.put("query", assetSearchDTO.getKeyword());
            body.put("query_type", "site");
            if (assetSearchDTO.getNext() != null) {
                body.put("next", assetSearchDTO.getNext());
            } else {
                body.put("page", assetSearchDTO.getPageNo());
            }
            body.put("pagesize", assetSearchDTO.getPageSize());
            body.put("zone_key_id", assetSearchEngine.getEngineToken());
            if (StringUtils.isNotBlank(assetSearchDTO.getTimestampSort())) {
                body.put("timestamp_sort", assetSearchDTO.getTimestampSort());
            }
            if (StringUtils.isNotBlank(assetSearchDTO.getExploreTimestampSort())) {
                body.put("explore_timestamp_sort", assetSearchDTO.getExploreTimestampSort());
            }
            if (assetSearchDTO.getZbPay() != null && assetSearchDTO.getZbPay()) {
                body.put("zb_pay", 1);
            }
            HttpResponse response = HttpUtils.post(baseUrl, headers, JSONObject.toJSONString(body));
            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody();
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
                if (jsonObject.getInteger("code") == 0) {
                    int total = Integer.parseInt(jsonObject.getString("total"));
                    IPage<AssetSearchVO> page = new Page<>(assetSearchDTO.getPageNo(), assetSearchDTO.getPageSize(), total);
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject assetObj = data.getJSONObject(i);
                        AssetSearchVO assetSearchVO = new AssetSearchVO();
                        assetSearchVO.setIp(assetObj.getString("ip"));
                        assetSearchVO.setUrl(assetObj.getString("url"));
                        assetSearchVO.setTitle(assetObj.getString("title"));
                        assetSearchVO.setCompany(assetObj.getString("group"));
                        assetSearchVO.setPort(Integer.valueOf(assetObj.getString("port")));
                        assetSearchVO.setBaseProtocol(assetObj.getString("protocol"));
                        assetSearchVO.setApp(assetObj.getString("component"));
                        assetSearchVO.setDomain(assetObj.getString("hostname"));
                        assetSearchVO.setIsWeb("Y");
                        assetSearchVO.setOs(assetObj.getString("os_name"));
                        assetSearchVO.setIsp(assetObj.getString("operator"));
                        assetSearchVO.setProvince(assetObj.getString("province"));
                        assetSearchVO.setCity(assetObj.getString("city"));
                        assetSearchVO.setCountry(assetObj.getString("country"));
                        assetSearchVO.setBanner(assetObj.getString("banner"));
                        assetSearchVO.setServer(assetObj.getString("component"));
                        if (StringUtils.isNotBlank(assetObj.getString("status_code"))) {
                            assetSearchVO.setStatusCode(Integer.parseInt(assetObj.getString("status_code")));
                        }
                        assetSearchVO.setProtocol(assetObj.getString("service"));
                        assetSearchVOList.add(assetSearchVO);
                    }
                    page.setRecords(assetSearchVOList);
                    JSONObject searchCount = jsonObject.getJSONObject("today_api_search_count");
                    return Result.OK("查询成功,剩余额度：" + searchCount.getString("site"), page);
                } else {
                    return Result.error(jsonObject.getInteger("code"), jsonObject.getString("message"));
                }
            } else {
                return Result.error(403, "查询失败，返回状态码:" + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("0Zone search error: {}", e.getMessage());
            return Result.error("0Zone search error: " + e.getMessage());
        }
    }
}
