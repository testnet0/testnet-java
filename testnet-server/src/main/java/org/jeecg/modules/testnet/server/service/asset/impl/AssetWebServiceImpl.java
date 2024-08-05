package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;
import org.jeecg.modules.testnet.server.mapper.asset.AssetPortMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.AssetWebVO;
import org.springframework.stereotype.Service;
import testnet.common.constan.Constants;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.utils.HttpUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @Description: WEB服务
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetWebServiceImpl extends ServiceImpl<AssetWebMapper, AssetWeb> implements IAssetService<AssetWeb, AssetWebVO, AssetWebDTO> {
    @Resource
    private IAssetValidService assetValidService;

    @Resource
    private AssetPortMapper assetPortMapper;

    @Resource
    private AssetApiServiceImpl assetApiService;


    @Resource
    private JeecgElasticsearchTemplate elasticsearchTemplate;

    @Override
    public IPage<AssetWeb> page(IPage<AssetWeb> page, QueryWrapper<AssetWeb> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("body")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("body", parameterMap.get("body")[0]);
            JSONObject match = new JSONObject();
            match.put("match", jsonObject);
            JSONObject finalJson = elasticsearchTemplate.buildQuery(null, match, (page.getCurrent() - 1) * page.getSize(), page.getSize());
            JSONObject result = elasticsearchTemplate.search(Constants.ES_WEB_INDEX, Constants.ES_WEB_TYPE, finalJson);
            JSONObject hits = result.getJSONObject("hits");
            if (hits != null && !hits.getJSONArray("hits").isEmpty()) {
                long totalHits = hits.getJSONObject("total").getLong("value");
                JSONArray jsonArray = hits.getJSONArray("hits");
                List<AssetWeb> assetWebList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i).getJSONObject("_source");
                    AssetWeb assetWeb = JSON.parseObject(json.toJSONString(), AssetWeb.class);
                    assetWebList.add(assetWeb);
                }
                IPage<AssetWeb> resultPage = new Page<>(page.getCurrent(), page.getSize());
                resultPage.setRecords(assetWebList);
                resultPage.setTotal(totalHits);
                return resultPage;
            } else {
                log.info("未查询到数据");
                queryWrapper.in("port_id", "-1");
            }
        }
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetWebVO convertVO(AssetWeb record) {
        AssetWebVO assetWebVO = new AssetWebVO();
        BeanUtil.copyProperties(record, assetWebVO, CopyOptions.create().setIgnoreNullValue(true));
        if (StringUtils.isNotBlank(record.getPortId())) {
            AssetPort assetPort = assetPortMapper.selectById(record.getPortId());
            if (assetPort != null) {
                assetWebVO.setIp(assetPort.getIp());
            }
        }
        return assetWebVO;
    }

    @Override
    public AssetWebDTO convertDTO(AssetWeb asset) {
        AssetWebDTO assetWebDTO = new AssetWebDTO();
        BeanUtil.copyProperties(asset, assetWebDTO, CopyOptions.create().setIgnoreNullValue(true));
        return assetWebDTO;
    }

    @Override
    public boolean addAssetByType(AssetWebDTO asset) {
        if (save(asset)) {
            saveAssetWebToES(asset);
            if (asset.getStatusCode().equals(200)) {
                AssetApiDTO assetApiDTO = new AssetApiDTO();
                assetApiDTO.setAbsolutePath(asset.getWebUrl());
                assetApiDTO.setTitle(asset.getWebTitle());
                assetApiDTO.setHttpMethod("GET");
                assetApiDTO.setProjectId(asset.getProjectId());
                assetApiDTO.setStatusCode(asset.getStatusCode());
                assetApiDTO.setContentLength(asset.getContentLength());
                assetApiService.addAssetByType(assetApiDTO);
            }
        }
        return true;
    }

    @Override
    public boolean updateAssetByType(AssetWebDTO asset) {
        if (StringUtils.isNotBlank(asset.getTech())) {
            // 合并资产标签
            AssetWeb assetWeb = getById(asset.getId());
            asset.setTech(mergeTechArrays(asset.getTech(), assetWeb.getTech()));
        }
        if (updateById(asset)) {
            saveAssetWebToES(asset);
        }
        return true;
    }

    @Override
    public void delRelation(List<String> list) {
        for (String id : list) {
            removeById(id);
            elasticsearchTemplate.delete(Constants.ES_WEB_INDEX, Constants.ES_WEB_TYPE, id);
        }
    }

    @Override
    public boolean saveBatch(Collection<AssetWeb> entityList) {
        List<AssetWeb> assetWebList = new ArrayList<>();
        for (AssetWeb assetWeb : entityList) {
            if (assetValidService.isValid(assetWeb, AssetTypeEnums.WEB)) {
                if (assetValidService.getUniqueAsset(assetWeb, this, AssetTypeEnums.WEB) == null) {
                    assetWebList.add(assetWeb);
                } else {
                    log.info("web:{} 重复，跳过", assetWeb);
                }
            }
        }
        return super.saveBatch(assetWebList);
    }


    public AssetWebVO getWebBody(String id) {
        try {
            JSONObject json = elasticsearchTemplate.getDataById(Constants.ES_WEB_INDEX, Constants.ES_WEB_TYPE, id);
            if (json != null) {
                return JSON.parseObject(json.toJSONString(), AssetWebVO.class);
            }
        } catch (Exception e) {
            log.info("getWebBody error", e);
            AssetWebVO assetWebVO = new AssetWebVO();
            AssetWeb assetWeb = getById(id);
            BeanUtil.copyProperties(assetWeb, assetWebVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            return assetWebVO;
        }
        return null;
    }

    private void saveAssetWebToES(AssetWebDTO assetWeb) {
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(assetWeb);
        try {
            if (assetWeb.getBody() != null && assetWeb.getBody().startsWith("http")) {
                String body = HttpUtils.get(assetWeb.getBody()).getBody().replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]");
                jsonObject.put("body", body);
            }
            elasticsearchTemplate.saveOrUpdate(Constants.ES_WEB_INDEX, Constants.ES_WEB_TYPE, assetWeb.getId(), jsonObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String mergeTechArrays(String newTechJson, String oldTechJson) {
        if (StringUtils.isBlank(newTechJson)) {
            return oldTechJson;
        }

        if (StringUtils.isBlank(oldTechJson)) {
            return newTechJson;
        }

        // 解析新的和旧的JSONArray
        JSONArray newTechArray = JSONArray.parseArray(newTechJson);
        JSONArray oldTechArray = JSONArray.parseArray(oldTechJson);
        Map<String, JSONObject> nameToTech = new HashMap<>();
        oldTechArray.forEach(tech -> {
            JSONObject techObject = (JSONObject) tech;
            String name = techObject.getString("name").toUpperCase();
            if (!nameToTech.containsKey(name)) {
                nameToTech.put(name, techObject);
            }
        });
        newTechArray.forEach(tech -> {
            JSONObject techObject = (JSONObject) tech;
            String name = techObject.getString("name").toUpperCase();
            if (!nameToTech.containsKey(name)) {
                nameToTech.put(name, techObject);
            } else {
                if (techObject.containsKey("version")) {
                    nameToTech.put(name, techObject);
                }
            }
        });
        return JSONArray.toJSONString(new ArrayList<>(nameToTech.values()));
    }

    public List<String> getByPortId(String id) {
        return baseMapper.findWebByPortId(id);
    }

    public List<String> getWebIdBySubDomainId(String id) {
        return baseMapper.getWebIdBySubDomainId(id);
    }

    public List<AssetWeb> getWebBySubDomainId(String id) {
        return baseMapper.getWebBySubDomainId(id);
    }
}
