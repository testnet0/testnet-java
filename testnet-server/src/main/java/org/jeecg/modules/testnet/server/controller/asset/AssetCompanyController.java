package org.jeecg.modules.testnet.server.controller.asset;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetCompany;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetCompanyServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 公司
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "公司")
@RestController
@RequestMapping("/testnet.server/assetCompany")
@Slf4j
public class AssetCompanyController extends JeecgController<AssetCompany, AssetCompanyServiceImpl> {
    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    /**
     * 分页列表查询
     *
     * @param assetCompany
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "公司-分页列表查询")
    @ApiOperation(value = "公司-分页列表查询", notes = "公司-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetCompany assetCompany, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetCompany, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.COMPANY));
    }

    /**
     * 添加
     *
     * @param assetCompany
     * @return
     */
    @AutoLog(value = "公司-添加")
    @ApiOperation(value = "公司-添加", notes = "公司-添加")
    @RequiresPermissions("testnet.server:asset_company:add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody AssetCompany assetCompany) {
        List<AssetCompany> assetCompanyList = new ArrayList<>();
        for (String s : assetCompany.getCompanyName().split("\n")) {
            // 创建一个新的AssetCompany对象
            AssetCompany company = new AssetCompany();
            BeanUtil.copyProperties(assetCompany, company);
            company.setCompanyName(s.trim());
            assetCompanyList.add(company);
        }
        Result<?> addResult = assetCommonOptionService.batchAdd(assetCompanyList, AssetTypeEnums.COMPANY);
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
     * @param assetCompany
     * @return
     */
    @AutoLog(value = "公司-编辑")
    @ApiOperation(value = "公司-编辑", notes = "公司-编辑")
    @RequiresPermissions("testnet.server:asset_company:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetCompany assetCompany) {
        Result<?> editResult = assetCommonOptionService.updateAssetByType(assetCompany, AssetTypeEnums.COMPANY);
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
    @AutoLog(value = "公司-通过id删除")
    @ApiOperation(value = "公司-通过id删除", notes = "公司-通过id删除")
    @RequiresPermissions("testnet.server:asset_company:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.COMPANY);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "公司-批量删除")
    @ApiOperation(value = "公司-批量删除", notes = "公司-批量删除")
    @RequiresPermissions("testnet.server:asset_company:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.COMPANY);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "公司-通过id查询")
    @ApiOperation(value = "公司-通过id查询", notes = "公司-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<? extends AssetBase> queryById(@RequestParam(name = "id", required = true) String id) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, AssetTypeEnums.COMPANY);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetCompany
     */
    @RequiresPermissions("testnet.server:asset_company:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetCompany assetCompany) {
        return super.exportXlsSheet(request, assetCompany, AssetCompany.class, "公司", null, 50000);
        // return super.exportXls(request, assetCompany, AssetCompany.class, "公司");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_company:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return assetCommonOptionService.importExcel(request, response, AssetCompany.class, AssetTypeEnums.COMPANY);
    }

}
