package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;

/**
 * @Description: 空间引擎配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IAssetSearchEngineService extends IService<AssetSearchEngine> {

    AssetSearchEngine getKey(String engine);
}
