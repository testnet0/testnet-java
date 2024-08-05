package org.jeecg.modules.testnet.server.service.asset;

import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import testnet.common.enums.AssetTypeEnums;

import java.util.Map;

public interface IAssetValidService {

    <T extends AssetBase> boolean isValid(T asset, AssetTypeEnums assetType);

    <T extends AssetBase> T getUniqueAsset(T asset, IAssetService<T, ? extends AssetBase, ? extends AssetBase> assetService, AssetTypeEnums assetType);

    Map<String, String> getUniqueCheckFieldName(AssetTypeEnums assetType);


}
