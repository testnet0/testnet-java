package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetWebServiceImpl;
import org.jeecg.modules.testnet.server.vo.AssetWebVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: WEB服务
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "WEB服务")
@RestController
@RequestMapping("/testnet.server/assetWeb")
@Slf4j
public class AssetWebController extends JeecgController<AssetWeb, AssetWebServiceImpl> {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private AssetWebServiceImpl assetWebService;

    /**
     * 分页列表查询
     *
     * @param assetWeb
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "WEB服务-分页列表查询")
    @ApiOperation(value = "WEB服务-分页列表查询", notes = "WEB服务-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetWeb assetWeb,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetWeb, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.WEB));
    }

    @ApiOperation(value = "WEB服务-查询返回包", notes = "WEB服务-查询返回包")
    @GetMapping(value = "/getWebBody")
    public Result<AssetWebVO> getWebBody(@RequestParam(name = "id", required = true) String id) {
        AssetWebVO assetWebVO = assetWebService.getWebBody(id);
        return Result.OK(assetWebVO);
    }


    /**
     * 添加
     *
     * @param assetWebDTO
     * @return
     */
    @AutoLog(value = "Web-添加")
    @ApiOperation(value = "Web-添加", notes = "Web-添加")
    @RequiresPermissions("testnet.server:asset_web:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetWebDTO assetWebDTO) {
        if (assetCommonOptionService.addAssetByType(assetWebDTO, AssetTypeEnums.WEB, true) != null) {
            return Result.OK("添加成功!");
        } else {
            return Result.error("添加失败，检查是否重复或缺少关键字段");
        }
    }

    /**
     * 编辑
     *
     * @param assetWebDTO
     * @return
     */
    @AutoLog(value = "Web-编辑")
    @ApiOperation(value = "Web-编辑", notes = "Web-编辑")
    @RequiresPermissions("testnet.server:asset_web:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetWebDTO assetWebDTO) {
        if (assetCommonOptionService.updateAssetByType(assetWebDTO, AssetTypeEnums.WEB) != null) {
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
    @AutoLog(value = "WEB服务-通过id删除")
    @ApiOperation(value = "WEB服务-通过id删除", notes = "WEB服务-通过id删除")
    @RequiresPermissions("testnet.server:asset_web:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.WEB);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "WEB服务-批量删除")
    @ApiOperation(value = "WEB服务-批量删除", notes = "WEB服务-批量删除")
    @RequiresPermissions("testnet.server:asset_web:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.WEB);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "WEB服务-通过id查询")
    @ApiOperation(value = "WEB服务-通过id查询", notes = "WEB服务-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AssetWeb> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetWeb assetWeb = assetCommonOptionService.getByIdAndAssetType(id, AssetTypeEnums.WEB);
        if (assetWeb == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(assetWeb);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetWeb
     */
    @RequiresPermissions("testnet.server:asset_web:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetWeb assetWeb) {
        return super.exportXls(request, assetWeb, AssetWeb.class, "WEB服务");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_web:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AssetWeb.class);
    }
}
