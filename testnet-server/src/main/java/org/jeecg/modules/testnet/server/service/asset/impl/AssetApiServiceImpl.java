package org.jeecg.modules.testnet.server.service.asset.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetApi;
import org.jeecg.modules.testnet.server.entity.asset.AssetApiTree;
import org.jeecg.modules.testnet.server.mapper.asset.AssetApiMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetApiTreeService;
import org.jeecg.modules.testnet.server.service.asset.IAssetService;
import org.jeecg.modules.testnet.server.service.asset.IAssetValidService;
import org.jeecg.modules.testnet.server.vo.AssetApiVO;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.utils.HashUtils;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 接口资产
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class AssetApiServiceImpl extends ServiceImpl<AssetApiMapper, AssetApi> implements IAssetService<AssetApi, AssetApiVO, AssetApiDTO> {

    @Resource
    private IAssetApiTreeService assetApiTreeService;
    @Resource
    private IAssetValidService assetValidService;

    @Override
    public IPage<AssetApi> page(IPage<AssetApi> page, QueryWrapper<AssetApi> queryWrapper, Map<String, String[]> parameterMap) {
        if (parameterMap != null && parameterMap.containsKey("pid")) {
            String parentId = parameterMap.get("pid")[0];
            if (!parentId.equals("0")) {
                // 计算耗时
                long startTime = System.nanoTime(); // 开始时间纳秒级
                String childId = assetApiTreeService.queryTreeChildIds(parentId);
                long endTime = System.nanoTime(); // 结束时间纳秒级
                long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                log.info("查询子树ID耗时：{} 毫秒", duration);
                queryWrapper.in("asset_web_tree_id", Arrays.asList(childId.split(",")));
            }
        }
        return super.page(page, queryWrapper);
    }

    @Override
    public AssetApiVO convertVO(AssetApi assetApi) {
        AssetApiVO assetApiVO = new AssetApiVO();
        BeanUtil.copyProperties(assetApi, assetApiVO, CopyOptions.create().setIgnoreNullValue(true));
        AssetApiTree assetApiTree = assetApiTreeService.getById(assetApiVO.getAssetWebTreeId());
        if (assetApiTree != null) {
            assetApiVO.setAbsolutePath(assetApiTree.getAbsolutePath());
        }
        return assetApiVO;
    }

    @Override
    public AssetApiDTO convertDTO(AssetApi asset) {
        AssetApiDTO dto = new AssetApiDTO();
        BeanUtil.copyProperties(asset, dto, CopyOptions.create().setIgnoreNullValue(true));
        AssetApiTree assetApiTree = assetApiTreeService.getById(asset.getAssetWebTreeId());
        if (assetApiTree != null) {
            dto.setAbsolutePath(assetApiTree.getAbsolutePath());
        }
        return dto;
    }

    @Override
    public boolean addAssetByType(AssetApiDTO assetApiDTO) {
        if (StringUtils.isNotBlank(assetApiDTO.getAbsolutePath())) {
            assetApiDTO.setPathMd5(HashUtils.calculateMD5(assetApiDTO.getProjectId() + assetApiDTO.getAbsolutePath() + assetApiDTO.getHttpMethod()));
            try {
                URL url = new URL(assetApiDTO.getAbsolutePath());
                String absolutePath;
                if (url.getPort() != -1) {
                    absolutePath = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
                } else {
                    absolutePath = url.getProtocol() + "://" + url.getHost();
                }
                String[] parts;
                String path = url.getPath();
                if (path.equals("/")) {
                    parts = new String[]{""};
                } else {
                    parts = path.split("/");
                }
                StringBuilder stringBuilder = new StringBuilder(absolutePath);
                AssetApiTree childAssetApiTree;
                String pid = "0";
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (i != 1) {
                        stringBuilder.append("/");
                    }
                    stringBuilder.append(part);
                    LambdaQueryWrapper<AssetApiTree> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(AssetApiTree::getAbsolutePath, stringBuilder.toString());
                    queryWrapper.eq(AssetApiTree::getPid, pid);
                    childAssetApiTree = assetApiTreeService.getOne(queryWrapper);
                    if (childAssetApiTree == null) {
                        childAssetApiTree = new AssetApiTree();
                        childAssetApiTree.setRelativePath(part);
                        childAssetApiTree.setPid(pid);
                        childAssetApiTree.setAbsolutePath(stringBuilder.toString());
                        childAssetApiTree.setProjectId(assetApiDTO.getProjectId());
                        childAssetApiTree.setSource(assetApiDTO.getSource());
                        if (i != parts.length - 1) {
                            childAssetApiTree.setHasChild("1");
                        } else {
                            childAssetApiTree.setHasChild("0");
                        }
                        assetApiTreeService.save(childAssetApiTree);
                    } else {
                        if (i != parts.length - 1 && !childAssetApiTree.getHasChild().equals("1")) {
                            childAssetApiTree.setHasChild("1");
                            assetApiTreeService.updateById(childAssetApiTree);
                        }
                    }
                    pid = childAssetApiTree.getId();
                }
                assetApiDTO.setAssetWebTreeId(pid);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return save(assetApiDTO);
    }

    @Override
    public boolean updateAssetByType(AssetApiDTO asset) {
        return updateById(asset);
    }

    @Override
    public void delRelation(List<String> list) {
        list.forEach(id -> {
            AssetApi assetApi = getById(id);
            if (assetApi != null) {
                removeById(id);
                // 递归删除tree表中节点及其空的父节点
                AssetApiTree assetApiTree = assetApiTreeService.getById(assetApi.getAssetWebTreeId());
                assetApiTreeService.deleteNodeAndEmptyParents(assetApiTree);
            }
        });
    }

    @Override
    public boolean saveBatch(Collection<AssetApi> entityList) {
        for (AssetApi assetApi : entityList) {
            if (StringUtils.isNotBlank(assetApi.getAssetWebTreeId())) {
                if (assetApi.getAssetWebTreeId().contains("http")) {
                    AssetApiDTO assetApiDTO = new AssetApiDTO();
                    BeanUtil.copyProperties(assetApi, assetApiDTO, CopyOptions.create().setIgnoreNullValue(true));
                    assetApiDTO.setAbsolutePath(assetApi.getAssetWebTreeId());
                    if (assetValidService.isValid(assetApiDTO, AssetTypeEnums.API).isSuccess()) {
                        this.addAssetByType(assetApiDTO);
                    }
                } else {
                    this.save(assetApi);
                }
            }
        }
        return true;
    }
}
