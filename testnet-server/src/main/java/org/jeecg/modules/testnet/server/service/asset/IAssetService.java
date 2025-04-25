package org.jeecg.modules.testnet.server.service.asset;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;

import java.util.List;
import java.util.Map;


public interface IAssetService<D extends AssetBase, V extends AssetBase, T extends AssetBase> extends IService<D> {

    IPage<D> page(IPage<D> page, QueryWrapper<D> queryWrapper, Map<String, String[]> parameterMap);

    List<D> list(QueryWrapper<D> queryWrapper,Map<String, String[]> queryObject);

    V convertVO(D record);

    T convertDTO(D asset);

    boolean addAssetByType(T asset);

    boolean updateAssetByType(T asset);

    void delRelation(List<String> list);

}
