package org.jeecg.modules.testnet.server.service.asset;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;

import java.util.List;
import java.util.Map;


public interface IAssetService<A extends AssetBase, V extends AssetBase, D extends AssetBase> extends IService<A> {

    IPage<A> page(IPage<A> page, QueryWrapper<A> queryWrapper, Map<String, String[]> parameterMap);

    V convertVO(A record);

    D convertDTO(A asset);

    boolean addAssetByType(D asset);

    boolean updateAssetByType(D asset);

    void delRelation(List<String> list);
}
