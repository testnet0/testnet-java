package org.jeecg.modules.testnet.server.controller.asset;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.asset.AssetVulDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetVul;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetVulServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 漏洞
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "漏洞")
@RestController
@RequestMapping("/testnet.server/assetVul")
@Slf4j
public class AssetVulController extends JeecgController<AssetVul, AssetVulServiceImpl> {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    /**
     * 分页列表查询
     *
     * @param assetVul
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "漏洞-分页列表查询")
    @Operation(summary = "漏洞-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetVul assetVul,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetVul, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.VUL));
    }

    /**
     * 添加
     *
     * @param assetVul
     * @return
     */
    @AutoLog(value = "漏洞-添加")
    @Operation(summary = "漏洞-添加")
    @RequiresPermissions("testnet.server:asset_vul:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetVulDTO assetVul) {
        Result<?> addResult = assetCommonOptionService.addAssetByType(assetVul, AssetTypeEnums.VUL, true);
        if (addResult.getCode().equals(200)) {
            return Result.OK("添加成功!");
        } else {
            JSONObject jsonObject = (JSONObject) addResult.getResult();
            return Result.OK(jsonObject.getString("errorMessage"));
        }
    }

    /**
     * 编辑
     *
     * @param assetVul
     * @return
     */
    @AutoLog(value = "漏洞-编辑")
    @Operation(summary = "漏洞-编辑")
    @RequiresPermissions("testnet.server:asset_vul:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetVulDTO assetVul) {
        Result<?> editResult = assetCommonOptionService.updateAssetByType(assetVul, AssetTypeEnums.VUL);
        if (editResult.getCode().equals(200)) {
            return Result.OK("编辑成功!");
        } else {
            JSONObject jsonObject = (JSONObject) editResult.getResult();
            return Result.OK(jsonObject.getString("errorMessage"));
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "漏洞-通过id删除")
    @Operation(summary = "漏洞-通过id删除")
    @RequiresPermissions("testnet.server:asset_vul:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.VUL);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "漏洞-批量删除")
    @Operation(summary = "漏洞-批量删除")
    @RequiresPermissions("testnet.server:asset_vul:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.VUL);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "漏洞-通过id查询")
    @Operation(summary = "漏洞-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<? extends AssetBase> queryById(@RequestParam(name = "id", required = true) String id) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, AssetTypeEnums.VUL);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetVul
     */
    @RequiresPermissions("testnet.server:asset_vul:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetVul assetVul) {
        // return super.exportXlsSheet(request, assetVul, AssetVul.class, "漏洞", null, 50000);
         return super.exportXls(request, assetVul, AssetVul.class, "漏洞");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_vul:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return assetCommonOptionService.importExcel(request, response, AssetVulDTO.class, AssetTypeEnums.VUL);
    }

}
