package org.jeecg.modules.testnet.server.service.asset;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;

import java.util.List;
import java.util.Map;


public interface IAssetService<D extends AssetBase, V extends AssetBase, T extends AssetBase> extends IService<D> {

    IPage<D> page(IPage<D> page, QueryWrapper<D> queryWrapper, Map<String, String[]> parameterMap);

    V convertVO(D record);

    T convertDTO(D asset);

    boolean addAssetByType(T asset);

    boolean updateAssetByType(T asset);

    void delRelation(List<String> list);

}
