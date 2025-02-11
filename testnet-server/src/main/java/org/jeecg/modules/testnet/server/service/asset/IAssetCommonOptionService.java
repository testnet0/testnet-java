package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import testnet.common.enums.AssetTypeEnums;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IAssetCommonOptionService {

    <D extends AssetBase> List<String> queryByAssetType(String params, String assetType);

    <D extends AssetBase> List<? extends AssetBase> queryAssetDOListByQueryAndAssetType(String params, String assetType);

    <D extends AssetBase> void delByIdAndAssetType(String assetId, AssetTypeEnums assetType);

    <D extends AssetBase> void delAssetByProjectId(String projectId, AssetTypeEnums assetType);

    <T extends AssetBase> long getCountByProjectId(String projectId, AssetTypeEnums assetType);

    <T extends AssetBase> Result<? extends AssetBase> addAssetByType(T asset, AssetTypeEnums assetType);

    <D extends AssetBase, T extends AssetBase> Result<? extends AssetBase> addAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate);

    <T extends AssetBase> Result<? extends AssetBase> updateAssetByType(T asset, AssetTypeEnums assetType);

    <T extends AssetBase> Result<? extends AssetBase> updateAssetByType(T asset, AssetTypeEnums assetType, boolean checkDuplicate);

    <T extends AssetBase, D extends AssetBase> Result<? extends AssetBase> addOrUpdate(T asset, AssetTypeEnums assetType, String taskId, String subTaskId);

    <T extends AssetBase> Result<? extends AssetBase> addOrUpdate(T asset, AssetTypeEnums assetType);

    <D extends AssetBase> long getCountByDate(AssetTypeEnums assetType);

    <D extends AssetBase> long getAllCountByAssetType(AssetTypeEnums assetType);

    <D extends AssetBase, V extends AssetBase> IPage<V> page(D asset, Integer pageNo, Integer pageSize, Map<String, String[]> parameterMap, AssetTypeEnums assetType);

    <D extends AssetBase> Result<? extends AssetBase> getAssetDOByIdAndAssetType(String id, AssetTypeEnums assetType);

    <D extends AssetBase> Result<? extends AssetBase> getByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType);

    <D extends AssetBase, T extends AssetBase> Result<List<? extends AssetBase>> listByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType);

    <D extends AssetBase, T extends AssetBase> Result<? extends AssetBase> getDTOByFieldAndAssetType(Map<String, String> fieldAndValue, AssetTypeEnums assetType);

    Class<? extends AssetBase> getAssetClassByType(AssetTypeEnums assetType);

    Class<? extends AssetBase> getAssetDTOClassByType(AssetTypeEnums assetType);

    void deleteAssetByQuery(String params);

    <T extends AssetBase> Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<? extends AssetBase> clazz, AssetTypeEnums assetType);

    <T extends AssetBase> Result<?> batchAdd(List<T> list, AssetTypeEnums assetType);

    <D extends AssetBase> void handleChangeLabels(String params);

    void handleChangeVulStatus(String params);


    <T extends AssetBase, D extends AssetBase> D getUniqueAsset(T asset, AssetTypeEnums assetType);

}
