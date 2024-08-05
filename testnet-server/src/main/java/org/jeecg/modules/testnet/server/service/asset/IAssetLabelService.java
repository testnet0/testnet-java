package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.AssetLabel;

/**
 * @Description: 资产标签
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IAssetLabelService extends IService<AssetLabel> {

    AssetLabel getAssetLabelByAssetName(String labelName);

    void update(AssetLabel assetLabel);

    void delete(AssetLabel assetLabel);
}
