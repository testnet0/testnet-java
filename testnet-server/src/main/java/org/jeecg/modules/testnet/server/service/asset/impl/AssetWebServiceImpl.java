package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;
import org.jeecg.modules.testnet.server.mapper.asset.AssetWebMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.vo.asset.AssetWebVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static testnet.common.utils.IpUtils.isValidIPAddress;

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
    private AssetPortServiceImpl assetPortService;

    @Resource
    private AssetApiServiceImpl assetApiService;

    @Resource
    private AssetIpServiceImpl assetIpService;

    @Resource
    private AssetSubDomainServiceImpl assetSubDomainService;

    @Override
    public IPage<AssetWeb> page(IPage<AssetWeb> page, QueryWrapper<AssetWeb> queryWrapper, Map<String, String[]> parameterMap) {
        queryGen(queryWrapper, parameterMap);
        return super.page(page, queryWrapper);
    }

    @Override
    public List<AssetWeb> list(QueryWrapper<AssetWeb> queryWrapper, Map<String, String[]> parameterMap) {
        queryGen(queryWrapper, parameterMap);
        return super.list(queryWrapper);
    }

    private void queryGen(QueryWrapper<AssetWeb> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("subdomain")) {
            queryWrapper.inSql("domain", "SELECT id from asset_sub_domain WHERE sub_domain LIKE '%" + parameterMap.get("subdomain")[0] + "%'");
        }
        if (parameterMap != null && parameterMap.containsKey("ip")) {
            queryWrapper.inSql("port_id", "select ap.id from asset_port ap LEFT JOIN asset_ip ai ON ap.ip = ai.id where ai.ip like '%" + parameterMap.get("ip")[0] + "%'");
        }
    }

    @Override
    public AssetWebVO convertVO(AssetWeb record) {
        AssetWebVO assetWebVO = new AssetWebVO();
        BeanUtil.copyProperties(record, assetWebVO, CopyOptions.create().setIgnoreNullValue(true));
        if (StringUtils.isNotBlank(record.getPortId())) {
            AssetPort assetPort = assetPortService.getById(record.getPortId());
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
        URLInfo urlInfo = extractURLInfo(asset.getWebUrl());
        if (urlInfo != null) {
            if (StringUtils.isNotBlank(urlInfo.getIp())) {
                // 说明是ip格式
                AssetIp assetIp = assetIpService.selectByIp(urlInfo.getIp(), asset.getProjectId());
                if (assetIp == null) {
                    AssetIpDTO assetIpDTO = new AssetIpDTO();
                    assetIpDTO.setIp(urlInfo.getIp());
                    assetIpDTO.setProjectId(asset.getProjectId());
                    assetIpDTO.setSource(asset.getSource());
                    try {
                        assetIpService.addAssetByType(assetIpDTO);
                        assetIp = new AssetIp();
                        assetIp.setId(assetIpDTO.getId());
                    } catch (Exception e) {
                        log.warn("主键冲突，重新查询资产: {}", asset, e);
                        assetIp = assetIpService.selectByIp(urlInfo.getIp(), asset.getProjectId());
                    }
                }
                AssetPort assetPort = assetPortService.getPortByIpIdAndPort(assetIp.getId(), urlInfo.getPort());
                if (assetPort == null) {
                    assetPort = new AssetPort();
                    assetPort.setIp(assetIp.getId());
                    assetPort.setIsOpen("Y");
                    assetPort.setPort(urlInfo.getPort());
                    assetPort.setProtocol(urlInfo.getProtocol());
                    // assetPort.setService(urlInfo.getDomain());
                    assetPort.setProjectId(asset.getProjectId());
                    assetPort.setSource(asset.getSource());
                    try {
                        assetPortService.save(assetPort);
                    } catch (Exception e) {
                        log.warn("主键冲突，重新查询资产: {}", asset, e);
                        assetPort = assetPortService.getPortByIpIdAndPort(assetIp.getId(), urlInfo.getPort());
                    }
                }
                asset.setPortId(assetPort.getId());
            }
            if (StringUtils.isNotBlank(urlInfo.getDomain())) {
                AssetSubDomain assetSubDomain = assetSubDomainService.selectBySubdomain(urlInfo.getDomain(), asset.getProjectId());
                if (assetSubDomain == null) {
                    AssetSubDomainIpsDTO assetSubDomainIpsDTO = new AssetSubDomainIpsDTO();
                    assetSubDomainIpsDTO.setIps(urlInfo.getIp());
                    assetSubDomainIpsDTO.setProjectId(asset.getProjectId());
                    assetSubDomainIpsDTO.setSubDomain(urlInfo.getDomain());
                    try {
                        assetSubDomainService.addAssetByType(assetSubDomainIpsDTO);
                        asset.setDomain(assetSubDomainIpsDTO.getId());
                    } catch (Exception e) {
                        log.warn("主键冲突，重新查询资产: {}", asset, e);
                        assetSubDomain = assetSubDomainService.selectBySubdomain(urlInfo.getDomain(), asset.getProjectId());
                        asset.setDomain(assetSubDomain.getId());
                    }
                } else {
                    AssetSubDomainIpsDTO assetSubDomainIpsDTO = assetSubDomainService.convertDTO(assetSubDomain);
                    if (StringUtils.isEmpty(assetSubDomainIpsDTO.getIps())) {
                        assetSubDomainIpsDTO.setIps(urlInfo.getIp());
                    } else {
                        if (!assetSubDomainIpsDTO.getIps().contains(urlInfo.getIp())) {
                            assetSubDomainIpsDTO.setIps(assetSubDomainIpsDTO.getIps() + "," + urlInfo.getIp());
                        }
                    }
                    assetSubDomainService.updateAssetByType(assetSubDomainIpsDTO);
                    asset.setDomain(assetSubDomainIpsDTO.getId());
                }
            }

        }
        if (save(asset)) {
//            if (asset.getStatusCode() != null && asset.getStatusCode().equals(200)) {
                AssetApiDTO assetApiDTO = new AssetApiDTO();
                assetApiDTO.setAbsolutePath(asset.getWebUrl());
                assetApiDTO.setTitle(asset.getWebTitle());
                assetApiDTO.setHttpMethod("GET");
                assetApiDTO.setProjectId(asset.getProjectId());
                assetApiDTO.setStatusCode(asset.getStatusCode());
                assetApiDTO.setContentLength(asset.getContentLength());
                assetApiService.addAssetByType(assetApiDTO);
           // }
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
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        for (String id : list) {
            removeById(id);
        }
    }

    private String mergeTechArrays(String newTechJson, String oldTechJson) {
        if (StringUtils.isBlank(newTechJson)) {
            return oldTechJson;
        }

        if (StringUtils.isBlank(oldTechJson)) {
            return newTechJson;
        }

        if (!newTechJson.startsWith("[")) {
            newTechJson = "[" + newTechJson + "]";
        }
        if (!oldTechJson.startsWith("[")) {
            oldTechJson = "[" + oldTechJson + "]";
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

    @Getter
    @Setter
    @AllArgsConstructor
    static class URLInfo {
        private String protocol;
        private String domain;
        private int port;
        private String ip;
    }

    /**
     * 通过域名获取IP地址
     */
    private static String getIPFromDomain(String domain) {
        try {
            InetAddress address = InetAddress.getByName(domain);
            return address.getHostAddress(); // 返回IP地址
        } catch (UnknownHostException e) {
            log.error("无法解析域名: {}", domain);
            return ""; // 解析失败返回空字符串
        }
    }

    private static URLInfo extractURLInfo(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            if (port == -1) {
                // 根据协议设置默认端口
                if ("http".equals(protocol)) {
                    port = 80;
                } else if ("https".equals(protocol)) {
                    port = 443;
                } else {
                    port = url.getDefaultPort();
                }
            }

            // 判断host是否是IP地址（IPv4或IPv6）
            String ip = "";
            String domain = "";
            // 去掉IPv6地址的方括号
            String cleanedHost = removeBrackets(host);
            if (isValidIPAddress(cleanedHost)) {
                ip = cleanedHost;
            } else {
                domain = cleanedHost;
                ip = getIPFromDomain(domain);
            }

            return new URLInfo(protocol, domain, port, ip);
        } catch (MalformedURLException e) {
            log.error("提供的URL格式不正确:{} ", e.getMessage());
            return null;
        }
    }

    /**
     * 去掉IPv6地址的方括号
     */
    private static String removeBrackets(String host) {
        if (host.startsWith("[") && host.endsWith("]")) {
            return host.substring(1, host.length() - 1);
        }
        return host;
    }

}
