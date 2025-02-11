package org.jeecg.modules.testnet.server.service.asset;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import testnet.common.enums.AssetTypeEnums;

import java.util.Map;

public interface IAssetValidService {

    <T extends AssetBase> Result<T> isValid(T asset, AssetTypeEnums assetType);

    Map<String, String> getUniqueCheckFieldName(AssetTypeEnums assetType);

    <D extends AssetBase> String getShaKey(D asset, AssetTypeEnums assetType);
}
