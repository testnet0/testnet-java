package org.jeecg.modules.testnet.server.service.liteflow;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;

import java.util.List;

/**
 * @Description: 流程管理
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IChainService extends IService<Chain> {

    List<Chain> getChainListByAssetType(String assetType);

    void clearCache();

    void copyChain(String chainId);

    void changeStatus(String field, String id, Boolean status);

    List<Chain> getAllChainList();

    Chain getByIdWithCache(String id);
}
