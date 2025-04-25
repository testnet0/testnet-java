package org.jeecg.modules.testnet.server.service.search.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.service.search.ISearchEngineService;
import org.jeecg.modules.testnet.server.vo.asset.AssetSearchVO;
import org.springframework.stereotype.Service;
import testnet.common.entity.HttpResponse;
import testnet.common.utils.HttpUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class FofaSearchEngineServiceImpl implements ISearchEngineService {
    @Override
    public Result<IPage<AssetSearchVO>> search(AssetSearchDTO assetSearchDTO, AssetSearchEngine assetSearchEngine) {
        List<AssetSearchVO> assetSearchVOList = new ArrayList<>();
        String fields = "ip,port,protocol,country_name,region,city,host,icp,title,banner,base_protocol";
        String fofaUrl = assetSearchEngine.getEngineHost();
        if (assetSearchDTO.getFull() != null && assetSearchDTO.getFull()) {
            fofaUrl += "&full=true";
        }
        try {
            HttpResponse response = HttpUtils.get(String.format(fofaUrl, assetSearchEngine.getEngineToken(), base64UrlEncode(assetSearchDTO.getKeyword()), assetSearchDTO.getPageNo(), assetSearchDTO.getPageSize(), fields));
            if (response.getStatusCode() == 200) {
                String body = response.getBody();
                JSONObject jsonObject = JSONObject.parseObject(body);
                log.info("FOFA查询结果：{}", body);
                if (!jsonObject.getBoolean("error")) { // Assuming 'error' is 0 when successful
                    JSONArray results = jsonObject.getJSONArray("results");
                    int total = jsonObject.getInteger("size");
                    IPage<AssetSearchVO> page = new Page<>(assetSearchDTO.getPageNo(), assetSearchDTO.getPageSize(), total);
                    page.setRecords(assetSearchVOList);

                    for (int i = 0; i < results.size(); i++) {
                        JSONArray assetArr = results.getJSONArray(i);
                        AssetSearchVO assetSearchVO = new AssetSearchVO();
                        assetSearchVO.setIp(assetArr.getString(0));
                        assetSearchVO.setPort(Integer.valueOf(assetArr.getString(1)));
                        assetSearchVO.setProtocol(assetArr.getString(2));
                        assetSearchVO.setCountry(assetArr.getString(3));
                        assetSearchVO.setRegion(assetArr.getString(4));
                        assetSearchVO.setCity(assetArr.getString(5));
                        String host = assetArr.getString(6);
                        if (StringUtils.isNotBlank(host) && (assetArr.getString(2).equals("http") || assetArr.getString(2).equals("https"))) {
                            assetSearchVO.setIsWeb("Y");
                            if (host.startsWith("https://")) {
                                assetSearchVO.setUrl(host);
                                host = host.replace("https://", "");
                            } else {
                                assetSearchVO.setUrl("http://" + host);
                            }
                        }
                        if (host.contains(":")) {
                            assetSearchVO.setDomain(host.split(":")[0]);
                        } else {
                            assetSearchVO.setDomain(host);
                        }
                        assetSearchVO.setIcpNumber(assetArr.getString(7));
                        assetSearchVO.setTitle(assetArr.getString(8));
                        assetSearchVO.setBanner(assetArr.getString(9));
                        assetSearchVO.setBaseProtocol(assetArr.getString(10));
                        assetSearchVOList.add(assetSearchVO);
                    }
                    return Result.OK("查询成功", page);
                } else {
                    return Result.error(403, "查询失败，原因：" + jsonObject.getString("errmsg"));
                }
            }
        } catch (IOException e) {
            log.error("FOFA search error: {}", e.getMessage());
            return Result.error("FOFA search error: " + e.getMessage());
        }
        return Result.error("查询失败！");
    }

    private String base64UrlEncode(String input) {
        String base64Encoded = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        String urlEncoded = null;
        try {
            urlEncoded = URLEncoder.encode(base64Encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return urlEncoded;
    }
}
