package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetIpServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: ip
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "ip")
@RestController
@RequestMapping("/testnet.server/assetIp")
@Slf4j
public class AssetIpController extends JeecgController<AssetIp, AssetIpServiceImpl> {


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    /**
     * 分页列表查询
     *
     * @param assetIp
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "ip-分页列表查询")
    @ApiOperation(value = "ip-分页列表查询", notes = "ip-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetIp assetIp, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetIp, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.IP));
    }

    /**
     * 添加
     *
     * @param assetIp
     * @return
     */
    @AutoLog(value = "ip-添加")
    @ApiOperation(value = "ip-添加", notes = "ip-添加")
    @RequiresPermissions("testnet.server:asset_ip:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetIpDTO assetIp) {
        if (assetCommonOptionService.addAssetByType(assetIp, AssetTypeEnums.IP) != null) {
            return Result.OK("添加成功!");
        } else {
            return Result.error("添加失败，检查是否重复或缺少关键字段");
        }
    }

    /**
     * 编辑
     *
     * @param assetIp
     * @return
     */
    @AutoLog(value = "ip-编辑")
    @ApiOperation(value = "ip-编辑", notes = "ip-编辑")
    @RequiresPermissions("testnet.server:asset_ip:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetIpDTO assetIp) {
        if (assetCommonOptionService.updateAssetByType(assetIp, AssetTypeEnums.IP) != null) {
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
    @AutoLog(value = "ip-通过id删除")
    @ApiOperation(value = "ip-通过id删除", notes = "ip-通过id删除")
    @RequiresPermissions("testnet.server:asset_ip:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.IP);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "ip-批量删除")
    @ApiOperation(value = "ip-批量删除", notes = "ip-批量删除")
    @RequiresPermissions("testnet.server:asset_ip:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.IP);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "ip-通过id查询")
    @ApiOperation(value = "ip-通过id查询", notes = "ip-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AssetIp> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetIp assetIp = assetCommonOptionService.getByIdAndAssetType(id, AssetTypeEnums.IP);
        if (assetIp == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(assetIp);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetIp
     */
    @RequiresPermissions("testnet.server:asset_ip:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetIp assetIp) {
        return super.exportXlsSheet(request, assetIp, AssetIp.class, "ip",null,50000);
        //return super.exportXls(request, assetIp, AssetIp.class, "ip");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_ip:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AssetIp.class);
    }

}
