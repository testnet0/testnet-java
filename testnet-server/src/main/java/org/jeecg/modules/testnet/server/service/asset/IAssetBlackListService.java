package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.AssetBlackList;

import java.util.List;

/**
 * @Description: 黑名单
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IAssetBlackListService extends IService<AssetBlackList> {

    List<AssetBlackList> getBlackList(String assType);

    void clearCache();
}
