package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetSubDomainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 子域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "子域名")
@RestController
@RequestMapping("/testnet.server/assetSubDomain")
@Slf4j
public class AssetSubDomainController extends JeecgController<AssetSubDomain, AssetSubDomainServiceImpl> {
    @Resource
    private IAssetCommonOptionService assetCommonOptionService;
    @Resource
    private IAssetIpSubdomainRelationService assetIpSubdomainRelationService;


    /**
     * 分页列表查询
     *
     * @param assetSubDomainIpsDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "子域名-分页列表查询")
    @ApiOperation(value = "子域名-分页列表查询", notes = "子域名-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetSubDomainIpsDTO assetSubDomainIpsDTO,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetSubDomainIpsDTO, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.SUB_DOMAIN));
    }

    /**
     * 添加
     */
    @AutoLog(value = "子域名-添加")
    @ApiOperation(value = "子域名-添加", notes = "子域名-添加")
    @RequiresPermissions("testnet.server:asset_sub_domain:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetSubDomainIpsDTO assetSubDomainIpsDTO) {
        if (assetCommonOptionService.addAssetByType(assetSubDomainIpsDTO, AssetTypeEnums.SUB_DOMAIN, true) != null) {
            return Result.OK("添加成功!");
        } else {
            return Result.error("添加失败，检查是否重复或缺少关键字段");
        }
    }

    /**
     * 编辑
     *
     * @param assetSubDomain
     * @return
     */
    @AutoLog(value = "子域名-编辑")
    @ApiOperation(value = "子域名-编辑", notes = "子域名-编辑")
    @RequiresPermissions("testnet.server:asset_sub_domain:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetSubDomainIpsDTO assetSubDomain) {
        assetIpSubdomainRelationService.delByAssetSubDomainId(assetSubDomain.getId());
        if (assetCommonOptionService.updateAssetByType(assetSubDomain, AssetTypeEnums.SUB_DOMAIN) != null) {
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
    @AutoLog(value = "子域名-通过id删除")
    @ApiOperation(value = "子域名-通过id删除", notes = "子域名-通过id删除")
    @RequiresPermissions("testnet.server:asset_sub_domain:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.SUB_DOMAIN);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "子域名-批量删除")
    @ApiOperation(value = "子域名-批量删除", notes = "子域名-批量删除")
    @RequiresPermissions("testnet.server:asset_sub_domain:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.SUB_DOMAIN);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "子域名-通过id查询")
    @ApiOperation(value = "子域名-通过id查询", notes = "子域名-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AssetSubDomain> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetSubDomain assetSubDomain = assetCommonOptionService.getByIdAndAssetType(id, AssetTypeEnums.SUB_DOMAIN);
        if (assetSubDomain == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(assetSubDomain);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetSubDomain
     */
    @RequiresPermissions("testnet.server:asset_sub_domain:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetSubDomain assetSubDomain) {

        //分sheet导出表格字段
        return super.exportXlsSheet(request, assetSubDomain, AssetSubDomain.class, "子域名",null,50000);
        // return super.exportXlsSheet(request, assetSubDomain, AssetSubDomain.class, "子域名");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_sub_domain:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AssetSubDomain.class);
    }

}
