package org.jeecg.modules.testnet.server.controller.asset;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetDomainServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 主域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "主域名")
@RestController
@RequestMapping("/testnet.server/assetDomain")
@Slf4j
public class AssetDomainController extends JeecgController<AssetDomain, AssetDomainServiceImpl> {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    /**
     * 分页列表查询
     *
     * @param assetDomain
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "主域名-分页列表查询")
    @Operation(summary = "主域名-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetDomain assetDomain, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetDomain, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.DOMAIN));
    }

    /**
     * 添加
     *
     * @param assetDomain
     * @return
     */
    @AutoLog(value = "主域名-添加")
    @Operation(summary = "主域名-添加")
    @RequiresPermissions("testnet.server:asset_domain:add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody AssetDomain assetDomain) {
        List<AssetDomain> assetDomainList = new ArrayList<>();
        for (String s : assetDomain.getDomain().split("\n")) {
            AssetDomain domain = new AssetDomain();
            BeanUtil.copyProperties(assetDomain, domain);
            domain.setDomain(s.trim());
            assetDomainList.add(domain);
        }
        Result<?> addResult = assetCommonOptionService.batchAdd(assetDomainList, AssetTypeEnums.DOMAIN);
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
     * @param assetDomain
     * @return
     */
    @AutoLog(value = "主域名-编辑")
    @Operation(summary = "主域名-编辑")
    @RequiresPermissions("testnet.server:asset_domain:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetDomain assetDomain) {
        Result<?> editResult = assetCommonOptionService.updateAssetByType(assetDomain, AssetTypeEnums.DOMAIN);
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
    @AutoLog(value = "主域名-通过id删除")
    @Operation(summary = "主域名-通过id删除")
    @RequiresPermissions("testnet.server:asset_domain:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.DOMAIN);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "主域名-批量删除")
    @Operation(summary = "主域名-批量删除")
    @RequiresPermissions("testnet.server:asset_domain:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.DOMAIN);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Operation(summary = "主域名-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<? extends AssetBase> queryById(@RequestParam(name = "id", required = true) String id) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, AssetTypeEnums.DOMAIN);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetDomain
     */
    @RequiresPermissions("testnet.server:asset_domain:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetDomain assetDomain) {
        // return super.exportXlsSheet(request, assetDomain, AssetDomain.class, "主域名", null, 50000);
        return super.exportXls(request, assetDomain, AssetDomain.class, "主域名");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_domain:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return assetCommonOptionService.importExcel(request, response, AssetDomain.class, AssetTypeEnums.DOMAIN);
    }

}
