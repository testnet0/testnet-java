package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import testnet.common.enums.AssetTypeEnums;

import java.util.List;
import java.util.Map;

public interface IAssetCommonOptionService {

    <T extends AssetBase> List<String> queryByAssetType(String params, String assetType);

    <T extends AssetBase> void delByIdAndAssetType(String assetId, AssetTypeEnums assetType);

    <T extends AssetBase> void delAssetByProjectId(String projectId, AssetTypeEnums assetType);

    <T extends AssetBase> long getCountByProjectId(String projectId, AssetTypeEnums assetType);

    <T extends AssetBase, R extends AssetBase> IPage<R> page(T asset, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap, AssetTypeEnums assetType);

    <T extends AssetBase> T addAssetByType(T asset, AssetTypeEnums assetType);

    <T extends AssetBase> T addAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate);

    <T extends AssetBase> T updateAssetByType(T asset, AssetTypeEnums assetType);

    <T extends AssetBase> T updateAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate);

    <T extends AssetBase> T addOrUpdate(T asset, AssetTypeEnums assetType, String taskId, String subTaskId);

    <T extends AssetBase> T addOrUpdate(T asset, AssetTypeEnums assetType);

    <T extends AssetBase> long getCountByDate(AssetTypeEnums assetType);

    <T extends AssetBase> long getAllCountByAssetType(AssetTypeEnums assetType);

    <T extends AssetBase> T getByIdAndAssetType(String id, AssetTypeEnums assetType);

    <T extends AssetBase> T getByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType);

    <T extends AssetBase> List<T> listByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType);

    <T extends AssetBase, R extends AssetBase> R getDTOByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType);

    Class<? extends AssetBase> getAssetClassByType(AssetTypeEnums assetType);

    void deleteAssetByQuery(String params);
}
