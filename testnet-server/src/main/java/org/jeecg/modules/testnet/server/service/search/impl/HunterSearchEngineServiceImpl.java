package org.jeecg.modules.testnet.server.service.search.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.service.search.ISearchEngineService;
import org.jeecg.modules.testnet.server.vo.AssetSearchVO;
import org.springframework.stereotype.Service;
import testnet.common.entity.HttpResponse;
import testnet.common.utils.HttpUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class HunterSearchEngineServiceImpl implements ISearchEngineService {

    @Override
    public Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, AssetSearchEngine assetSearchEngine) {
        List<AssetSearchVO> assetSearchVOList = new ArrayList<>();
        String baseUrl = assetSearchEngine.getEngineHost();

        // 构建完整的URL，包括查询参数
        String hunterUrl = buildUrl(baseUrl, assetSearchDTO, assetSearchEngine.getEngineToken());

        try {

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            // 发起GET请求
            HttpResponse response = HttpUtils.get(hunterUrl, headers);
            if (response.getStatusCode() == 200) {
                String body = response.getBody();
                JSONObject jsonObject = JSONObject.parseObject(body);
                log.info("Hunter查询结果：{}", body);
                if (jsonObject.getInteger("code") == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int total = data.getInteger("total");
                    IPage<AssetSearchVO> page = new Page<>(assetSearchDTO.getPageNo(), assetSearchDTO.getPageSize(), total);
                    // 设置记录
                    JSONArray arr = data.getJSONArray("arr");
                    if (arr != null) {
                        for (int i = 0; i < arr.size(); i++) {
                            JSONObject assetObj = arr.getJSONObject(i);
                            AssetSearchVO assetSearchVO = JSON.parseObject(assetObj.toJSONString(), AssetSearchVO.class);
                            assetSearchVO.setIsWeb(assetObj.getString("is_web").equals("是") ? "Y" : "N");
                            assetSearchVO.setIcpNumber(assetObj.getString("number"));
                            assetSearchVO.setBaseProtocol(assetObj.getString("base_protocol"));
                            assetSearchVO.setTitle(assetObj.getString("web_title"));
                            assetSearchVOList.add(assetSearchVO);
                        }
                    }
                    page.setRecords(assetSearchVOList);
                    String consumeQuota = data.getString("consume_quota");
                    String restQuota = data.getString("rest_quota");
                    return Result.OK("查询成功," + consumeQuota + "," + restQuota, page);
                } else {
                    return Result.error(403, "查询失败，请检查Key是否配置正确！错误信息：" + jsonObject.getString("message"));
                }
            }
        } catch (IOException e) {
            log.error("Hunter search error: {}", e.getMessage());
            return Result.error("Hunter search error: " + e.getMessage());
            // 使用合适的异常处理方式
        }
        return Result.error("查询失败！");
    }

    private String buildUrl(String baseUrl, AssetSearchDTO assetSearchDTO, String key) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("?api-key=").append(key);
        urlBuilder.append("&search=").append(base64UrlEncode(assetSearchDTO.getKeyword()));
        urlBuilder.append("&page=").append(assetSearchDTO.getPageNo());
        urlBuilder.append("&page_size=").append(assetSearchDTO.getPageSize());
        // 可选参数
        if (StringUtils.isNotBlank(assetSearchDTO.getStartTime())) {
            urlBuilder.append("&start_time=").append(assetSearchDTO.getStartTime());
        }
        if (StringUtils.isNotBlank(assetSearchDTO.getEndTime())) {
            urlBuilder.append("&end_time=").append(assetSearchDTO.getEndTime());
        }
        if (assetSearchDTO.getIsWeb() != null) {
            urlBuilder.append("&is_web=").append(assetSearchDTO.getIsWeb());
        }
        if (StringUtils.isNotBlank(assetSearchDTO.getStatusCode())) {
            urlBuilder.append("&status_code=").append(assetSearchDTO.getStatusCode());
        }
        if (assetSearchDTO.getPortFilter() != null) {
            urlBuilder.append("&port_filter=").append(assetSearchDTO.getPortFilter());
        }
        return urlBuilder.toString();
    }

    private String base64UrlEncode(String input) {
        String encoded = Base64.getUrlEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        return encoded.replaceAll("=", "%3D");
    }
}
