/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.asset.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.testnet.server.entity.asset.AssetApi;
import org.jeecg.modules.testnet.server.entity.asset.AssetApiTree;
import org.jeecg.modules.testnet.server.mapper.asset.AssetApiMapper;
import org.jeecg.modules.testnet.server.mapper.asset.AssetApiTreeMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetApiTreeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
@Slf4j
public class AssetApiTreeServiceImpl extends ServiceImpl<AssetApiTreeMapper, AssetApiTree> implements IAssetApiTreeService {


    @Resource
    private AssetApiMapper assetApiMapper;


    @Resource
    private AssetApiTreeCacheServiceImpl assetApiTreeCacheService;


    /**
     * 递归查询节点的根节点
     *
     * @param pidVal
     * @return
     */
    private AssetApiTree getTreeRoot(String pidVal) {
        AssetApiTree data = assetApiTreeCacheService.selectById(pidVal);
        if (data != null && !ROOT_PID_VALUE.equals(data.getPid())) {
            return this.getTreeRoot(data.getPid());
        } else {
            return data;
        }
    }


    /**
     * 根据id查询所有子节点id
     *
     * @param ids
     * @return
     */
    @Override
    public String queryTreeChildIds(String ids) {
        //获取id数组
        StringBuffer sb = new StringBuffer();
        if (!sb.toString().contains(ids)) {
            if (sb.toString().length() > 0) {
                sb.append(",");
            }
            sb.append(ids);
        }
        this.getTreeChildIds(ids, sb);
        return sb.toString();
    }

    @Override
    public List<SelectTreeModel> queryListByPid(String pid, Map<String, String> query) {
        if (oConvertUtils.isEmpty(pid)) {
            pid = ROOT_PID_VALUE;
        }
        return baseMapper.queryListByPid(pid, query);
    }

    @Override
    public IPage<SelectTreeModel> getRootTree(String keyword, String id, Integer pageNo, Integer pageSize) {
        QueryWrapper<AssetApiTree> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(id)) {
            queryWrapper.eq("id", id);
        }
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("relative_path", keyword);
            List<AssetApiTree> dataList = baseMapper.selectList(queryWrapper);
            Set<AssetApiTree> allNodes = new HashSet<>();
            for (AssetApiTree node : dataList) {
                AssetApiTree rootNode = getTreeRoot(node.getId());
                if (rootNode != null) {
                    allNodes.add(rootNode);
                }
            }
            List<SelectTreeModel> selectTreeModels = new ArrayList<>();
            for (AssetApiTree node : allNodes) {
                selectTreeModels.add(buildSelectTreeModel(node));
            }
            IPage<SelectTreeModel> selectTreeModelIPage = new Page<>(pageNo, pageSize, selectTreeModels.size());
            selectTreeModelIPage.setRecords(selectTreeModels);
            return selectTreeModelIPage;
        } else {
            queryWrapper.eq("pid", ROOT_PID_VALUE);
            Page<AssetApiTree> page = new Page<>(pageNo, pageSize);
            IPage<AssetApiTree> pageList = page(page, queryWrapper);
            IPage<SelectTreeModel> selectTreeModelIPage = new Page<>(pageNo, pageSize, pageList.getTotal());
            List<SelectTreeModel> selectTreeModels = new ArrayList<>();
            pageList.getRecords().forEach(record -> {
                selectTreeModels.add(buildSelectTreeModel(record));
            });
            selectTreeModelIPage.setRecords(selectTreeModels);
            return selectTreeModelIPage;
        }
    }

    @Override
    public List<SelectTreeModel> getChildTree(String pid) {
        if (pid == null) {
            pid = ROOT_PID_VALUE;
        }
        QueryWrapper<AssetApiTree> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("absolute_path");
        queryWrapper.eq("pid", pid);
        List<AssetApiTree> dataList = baseMapper.selectList(queryWrapper);
        Set<AssetApiTree> allNodes = new HashSet<>(dataList);
        Set<String> pidIdList = new HashSet<>();
        for (AssetApiTree node : dataList) {
            // 获取父级
            if (!pidIdList.contains(node.getPid())) {
                pidIdList.add(node.getPid());
                allNodes.add(assetApiTreeCacheService.selectById(node.getPid()));
            }
            // 获取子集
            allNodes.addAll(assetApiTreeCacheService.selectByPId(node.getId()));
        }
        List<SelectTreeModel> selectTreeModels = new ArrayList<>();
        for (AssetApiTree node : allNodes) {
            if (node.getPid().equals(pid)) {
                selectTreeModels.add(buildSelectTreeModel(node));
            }
        }
        return selectTreeModels;
    }

    @Override
    public AssetApiTree getByWebId(String baseAssetId) {
        LambdaQueryWrapper<AssetApiTree> queryWrapper = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(baseAssetId)) {
            queryWrapper.eq(AssetApiTree::getAssetWebId, baseAssetId);
            queryWrapper.eq(AssetApiTree::getRelativePath, "/");
            return getOne(queryWrapper);
        }
        return null;
    }

    @Override
    // @Async
    public void del(String ids) {
        List<AssetApiTree> assetApiTrees = this.listByIds(Arrays.asList(ids.split(",")));
        for (AssetApiTree assetApiTree : assetApiTrees) {
            String allIds = queryTreeChildIds(assetApiTree.getId());
            if (StringUtils.isNotBlank(allIds)) {
                assetApiTreeCacheService.deleteBatchIds(Arrays.asList(ids.split(",")));
                for (String s : allIds.split(",")) {
                    assetApiMapper.delete(new QueryWrapper<AssetApi>().eq("asset_web_tree_id", s));
                }
            }
        }
    }

    private SelectTreeModel buildSelectTreeModel(AssetApiTree assetApiTree) {
        SelectTreeModel selectTreeModel = new SelectTreeModel();
        if (assetApiTree.getPid().equals(ROOT_PID_VALUE)) {
            selectTreeModel.setTitle(assetApiTree.getAbsolutePath());
        } else {
            selectTreeModel.setTitle("/" + assetApiTree.getRelativePath());
        }
        selectTreeModel.setParentId(assetApiTree.getPid());
        selectTreeModel.setKey(assetApiTree.getId());
        selectTreeModel.setValue(assetApiTree.getId());
        selectTreeModel.setLeaf(!assetApiTree.getHasChild().equals("1"));
        return selectTreeModel;
    }

    /**
     * 递归查询所有子节点
     *
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal, StringBuffer sb) {
        List<AssetApiTree> dataList = assetApiTreeCacheService.selectByPId(pidVal);
        if (dataList != null && dataList.size() > 0) {
            StringBuilder sb2 = new StringBuilder();
            for (AssetApiTree tree : dataList) {
                if (!sb.toString().contains(tree.getId())) {
                    sb.append(",").append(tree.getId());
                }
                if (tree.getHasChild().equals("1")) {
                    if (sb2.toString().length() > 0) {
                        sb2.append(",");
                    }
                    sb2.append(tree.getId());
                }
            }
            if (sb2.length() > 0) {
                getTreeChildIds(sb2.toString(), sb);
            }
        }
        return sb;
    }

    public List<AssetApiTree> getAncestorsAndDescendants(String id) {
        Set<AssetApiTree> result = new HashSet<>();
        getAncestors(id, result);
        getDescendants(id, result);
        return new ArrayList<>(result);
    }

    public void getAncestors(String id, Set<AssetApiTree> result) {
        AssetApiTree node = assetApiTreeCacheService.selectById(id);
        if (node != null && result.add(node) && !"0".equals(node.getPid())) {
            getAncestors(node.getPid(), result);
        }
    }

    public void getDescendants(String id, Set<AssetApiTree> result) {
        List<AssetApiTree> children = assetApiTreeCacheService.selectByPId(id);
        for (AssetApiTree child : children) {
            if (result.add(child)) {
                getDescendants(child.getId(), result);
            }
        }
    }


    /**
     * 判断节点树的子节点树是否为空
     *
     * @param assetApiTree 节点
     * @return 如果节点树的子节点树为空则返回true，否则返回false
     */
    private boolean isChildNodeEmpty(AssetApiTree assetApiTree) {
        QueryWrapper<AssetApiTree> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", assetApiTree.getId());
        List<AssetApiTree> children = list(queryWrapper);
        return children == null || children.isEmpty();
    }

    /**
     * 判断节点的子节点列表是否为空
     *
     * @param assetApiTree 节点
     * @return 如果子节点列表为空则返回true，否则返回false
     */
    private boolean isParentNodeEmpty(AssetApiTree assetApiTree) {
        // 使用查询包装器查询子节点
        QueryWrapper<AssetApi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_web_tree_id", assetApiTree.getId());
        List<AssetApi> children = assetApiMapper.selectList(queryWrapper);
        QueryWrapper<AssetApiTree> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("pid", assetApiTree.getPid());
        List<AssetApiTree> children2 = list(queryWrapper2);
        return (children == null || children.isEmpty()) && (children2 == null || children2.isEmpty());
    }

    @Override
    public void deleteNodeAndEmptyParents(AssetApiTree assetApiTree) {
        if (assetApiTree != null) {
            if (isChildNodeEmpty(assetApiTree)) {
                assetApiTreeCacheService.deleteById(assetApiTree.getId());
            }
            // 检查父节点的子节点是否为空
            if (isParentNodeEmpty(assetApiTree)) {
                // 如果为空，则递归删除父节点
                if (!assetApiTree.getPid().equals("0")) {
                    assetApiTree = getById(assetApiTree.getPid());
                    deleteNodeAndEmptyParents(assetApiTree);
                }
            }
        }
    }

}
