package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "漏洞")
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
    @ApiOperation(value = "漏洞-分页列表查询", notes = "漏洞-分页列表查询")
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
    @ApiOperation(value = "漏洞-添加", notes = "漏洞-添加")
    @RequiresPermissions("testnet.server:asset_vul:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetVulDTO assetVul) {
        if (assetCommonOptionService.addAssetByType(assetVul, AssetTypeEnums.VUL, true) != null) {
            return Result.OK("添加成功!");
        } else {
            return Result.error("添加失败，检查是否重复或缺少关键字段");
        }
    }

    /**
     * 编辑
     *
     * @param assetVul
     * @return
     */
    @AutoLog(value = "漏洞-编辑")
    @ApiOperation(value = "漏洞-编辑", notes = "漏洞-编辑")
    @RequiresPermissions("testnet.server:asset_vul:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetVulDTO assetVul) {
        if (assetCommonOptionService.updateAssetByType(assetVul, AssetTypeEnums.VUL) != null) {
            return Result.OK("编辑成功!");
        } else {
            return Result.error("编辑失败，检查是否重复或缺少关键字段");
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "漏洞-通过id删除")
    @ApiOperation(value = "漏洞-通过id删除", notes = "漏洞-通过id删除")
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
    @ApiOperation(value = "漏洞-批量删除", notes = "漏洞-批量删除")
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
    @ApiOperation(value = "漏洞-通过id查询", notes = "漏洞-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AssetVul> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetVul assetVul = assetCommonOptionService.getByIdAndAssetType(id, AssetTypeEnums.VUL);
        if (assetVul == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(assetVul);
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
        return super.importExcel(request, response, AssetVul.class);
    }

}
