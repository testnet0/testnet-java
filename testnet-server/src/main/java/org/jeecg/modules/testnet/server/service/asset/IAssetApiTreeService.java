package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.testnet.server.entity.asset.AssetApiTree;

import java.util.List;
import java.util.Map;

public interface IAssetApiTreeService extends IService<AssetApiTree> {

    /**
     * 根节点父ID的值
     */
    public static final String ROOT_PID_VALUE = "0";
    /**
     * 树节点有子节点状态值
     */
    public static final String HASCHILD = "1";
    /**
     * 树节点无子节点状态值
     */
    public static final String NOCHILD = "0";


    String queryTreeChildIds(String ids);

    List<SelectTreeModel> queryListByPid(String parentId, Map<String, String> query);

    IPage<SelectTreeModel> getRootTree(String keyword, String webId, Integer pageNo, Integer pageSize);

    List<SelectTreeModel> getChildTree(String pid);

    AssetApiTree getByWebId(String baseAssetId);

    void del(String ids);

    void deleteNodeAndEmptyParents(AssetApiTree assetApiTree);
}
