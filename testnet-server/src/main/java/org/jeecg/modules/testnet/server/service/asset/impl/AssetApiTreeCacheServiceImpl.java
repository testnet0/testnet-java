package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.testnet.server.entity.asset.AssetApiTree;
import org.jeecg.modules.testnet.server.mapper.asset.AssetApiMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetApiTreeMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class AssetApiTreeCacheServiceImpl extends ServiceImpl<AssetApiTreeMapper, AssetApiTree> {


    @Cacheable(value = "asset:api_tree_id::cache", key = "#id", unless = "#result == null ")
    public AssetApiTree selectById(String id) {
        return baseMapper.selectById(id);
    }

    public List<AssetApiTree> selectByPId(String pids) {
        return baseMapper.selectIdAndPidList(Arrays.asList(pids.split(",")));
    }

    @CacheEvict(value = "asset:api_tree_id::cache", key = "#id")
    public void deleteById(String id) {
        baseMapper.deleteById(id);
    }

    @CacheEvict(value = "asset:api_tree_id::cache", allEntries = true)
    public void deleteBatchIds(List<String> ids) {
        baseMapper.deleteBatchIds(ids);
    }

}
