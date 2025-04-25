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
import org.jeecg.modules.testnet.server.dto.asset.AssetWebDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetWebServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: WEB服务
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "WEB服务")
@RestController
@RequestMapping("/testnet.server/assetWeb")
@Slf4j
public class AssetWebController extends JeecgController<AssetWeb, AssetWebServiceImpl> {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


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
    @Operation(summary = "WEB服务-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetWeb assetWeb,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetWeb, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.WEB));
    }

    /**
     * 添加
     *
     * @param assetWebDTO
     * @return
     */
    @AutoLog(value = "Web-添加")
    @Operation(summary = "Web-添加")
    @RequiresPermissions("testnet.server:asset_web:add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody AssetWebDTO assetWebDTO) {
        List<AssetWebDTO> assetWebList = new ArrayList<>();
        for (String s : assetWebDTO.getWebUrl().split("\n")) {
            // 创建一个新的AssetWebDTO对象
            AssetWebDTO web = new AssetWebDTO();
            BeanUtil.copyProperties(assetWebDTO, web);
            web.setWebUrl(s.trim());
            assetWebList.add(web);
        }
        Result<?> addResult = assetCommonOptionService.batchAdd(assetWebList, AssetTypeEnums.WEB);
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
     * @param assetWebDTO
     * @return
     */
    @AutoLog(value = "Web-编辑")
    @Operation(summary = "Web-编辑")
    @RequiresPermissions("testnet.server:asset_web:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetWebDTO assetWebDTO) {
        Result<?> editResult = assetCommonOptionService.updateAssetByType(assetWebDTO, AssetTypeEnums.WEB);
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
    @AutoLog(value = "WEB服务-通过id删除")
    @Operation(summary = "WEB服务-通过id删除")
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
    @Operation(summary = "WEB服务-批量删除")
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
    @Operation(summary = "WEB服务-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<? extends AssetBase> queryById(@RequestParam(name = "id", required = true) String id) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, AssetTypeEnums.WEB);
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
        // return super.exportXlsSheet(request, assetWeb, AssetWeb.class, "WEB服务", null, 50000);
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
       return assetCommonOptionService.importExcel(request, response, AssetWebDTO.class, AssetTypeEnums.WEB);
    }
}
