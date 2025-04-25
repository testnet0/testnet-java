package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.AssetApiDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetApi;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetApiServiceImpl;
import org.jeecg.modules.testnet.server.vo.asset.AssetApiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.utils.HashUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: API
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "API")
@RestController
@RequestMapping("/testnet.server/assetApi")
@Slf4j
public class AssetApiController extends JeecgController<AssetApi, AssetApiServiceImpl> {
    @Autowired
    private IAssetCommonOptionService assetCommonOptionService;


    @Operation(summary = "API-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AssetApiVO>> list(AssetApi assetApi,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetApi, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.API));
    }

    /**
     * 添加
     *
     * @param assetApiDTO
     * @return
     */
    // @AutoLog(value = "API-添加")
    @Operation(summary = "API-添加")
    @RequiresPermissions("testnet.server:asset_api:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetApiDTO assetApiDTO) {
        assetApiDTO.setPathMd5(HashUtils.calculateMD5(assetApiDTO.getProjectId() + assetApiDTO.getAbsolutePath() + assetApiDTO.getHttpMethod()));
        Result<?> result = assetCommonOptionService.addAssetByType(assetApiDTO, AssetTypeEnums.API, true);
        if (result.isSuccess()) {
            return Result.OK("添加成功!");
        } else {
            return Result.error(result.getMessage());
        }
    }

    /**
     * 编辑
     *
     * @param assetApiDTO
     * @return
     */
    // @AutoLog(value = "API-编辑")
    @Operation(summary = "API-编辑")
    @RequiresPermissions("testnet.server:asset_api:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetApiDTO assetApiDTO) {
        assetApiDTO.setAbsolutePath(assetApiDTO.getAssetWebTreeId());
        Result<?> result = assetCommonOptionService.updateAssetByType(assetApiDTO, AssetTypeEnums.API, false);
        if (result.isSuccess()) {
            return Result.OK("编辑成功!");
        } else {
            return Result.error(result.getMessage());
        }
    }

    /**
     * 通过id删除
     * 通过id删除
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "API-通过id删除")
    @Operation(summary = "API-通过id删除")
    @RequiresPermissions("testnet.server:asset_api:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.API);
        return Result.OK("删除成功!");

    }

    /**
     * 批量删除
     * 批量删除
     *
     * @param ids
     * @return
     */
    //@AutoLog(value = "API-批量删除")
    @Operation(summary = "API-批量删除")
    @RequiresPermissions("testnet.server:asset_api:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.API);
        return Result.OK("删除成功!");
    }


    /**
     * 导出excel
     *
     * @param request
     * @param assetApi
     */
    @RequiresPermissions("testnet.server:asset_api:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetApi assetApi) {
        // return super.exportXlsSheet(request, assetApi, AssetApi.class, "API", null, 50000);
        return super.exportXls(request, assetApi, AssetApi.class, "API");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_api:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return assetCommonOptionService.importExcel(request, response, AssetApiDTO.class, AssetTypeEnums.API);
    }
}
