package org.jeecg.modules.testnet.server.service.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Service
@Slf4j
public class ShodanSearchEngineServiceImpl implements ISearchEngineService {

    @Override
    public Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, AssetSearchEngine assetSearchEngine) {
        List<AssetSearchVO> assetSearchVOList = new ArrayList<>();
        String shodanUrl = assetSearchEngine.getEngineHost();
        try {
            HttpResponse response = HttpUtils.get(String.format(shodanUrl, assetSearchEngine.getEngineToken(), assetSearchDTO.getKeyword(), assetSearchDTO.getPageNo()));
            if (response.getStatusCode() == 200) {
                String body = response.getBody();
                JSONObject jsonObject = JSONObject.parseObject(body);
                log.info("Shodan查询结果：{}", body);
                JSONArray results = jsonObject.getJSONArray("matches");
                int total = jsonObject.getInteger("total");
                IPage<AssetSearchVO> page = new Page<>(assetSearchDTO.getPageNo(), assetSearchDTO.getPageSize(), total);
                page.setRecords(assetSearchVOList);
                for (int i = 0; i < results.size(); i++) {
                    JSONObject assetObj = results.getJSONObject(i);
                    AssetSearchVO assetSearchVO = new AssetSearchVO();
                    assetSearchVO.setIp(assetObj.getString("ip_str"));
                    assetSearchVO.setPort(assetObj.getInteger("port"));
                    assetSearchVO.setProtocol(assetObj.getString("transport"));
                    assetSearchVO.setCountry(assetObj.getJSONObject("location").getString("country_name"));
                    assetSearchVO.setRegion(assetObj.getJSONObject("location").getString("region_code"));
                    assetSearchVO.setCity(assetObj.getJSONObject("location").getString("city"));
                    List<String> assetSubDomainList = new ArrayList<>();
                    assetObj.getJSONArray("hostnames").forEach(domain -> {
                        assetSubDomainList.add(domain.toString());
                    });
                    JSONObject http = assetObj.getJSONObject("http");
                    if (http != null) {
                        assetSearchVO.setStatusCode(http.getInteger("status"));
                        assetSearchVO.setTitle(http.getString("title"));
                        assetSearchVO.setBody(http.getString("body"));
                        JSONObject components = http.getJSONObject("components");
                        JSONArray component = new JSONArray();
                        if (components != null) {
                            for (String name : components.keySet()) {
                                JSONObject tmp = new JSONObject();
                                tmp.put("name", name);
                                component.add(tmp);
                            }
                            assetSearchVO.setComponent(component.toJSONString());
                        }
                    }
                    assetSearchVO.setDomains(assetSubDomainList);
                    assetSearchVO.setBanner(assetObj.getString("data"));
                    assetSearchVO.setBaseProtocol(assetObj.getString("transport"));
                    assetSearchVO.setUrl(assetObj.getString("http.url"));
                    assetSearchVOList.add(assetSearchVO);
                }
                page.setSize(100);
                page.setCurrent(assetSearchDTO.getPageNo());
                return Result.OK("查询成功", page);

            }
        } catch (IOException e) {
            log.error("Shodan search error: {}", e.getMessage());
            return Result.error("Shodan search error: " + e.getMessage());
        }
        return Result.error("查询失败！");
    }
}
