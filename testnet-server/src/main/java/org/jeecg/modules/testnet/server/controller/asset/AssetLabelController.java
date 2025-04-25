package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.testnet.server.entity.asset.AssetLabel;
import org.jeecg.modules.testnet.server.service.asset.IAssetLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 资产标签
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "资产标签")
@RestController
@RequestMapping("/testnet.server/assetLabel")
@Slf4j
public class AssetLabelController extends JeecgController<AssetLabel, IAssetLabelService> {
    @Autowired
    private IAssetLabelService assetLabelService;

    /**
     * 分页列表查询
     *
     * @param assetLabel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "资产标签-分页列表查询")
    @Operation(summary = "资产标签-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AssetLabel>> queryPageList(AssetLabel assetLabel,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<AssetLabel> queryWrapper = QueryGenerator.initQueryWrapper(assetLabel, req.getParameterMap());
        Page<AssetLabel> page = new Page<AssetLabel>(pageNo, pageSize);
        IPage<AssetLabel> pageList = assetLabelService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param assetLabel
     * @return
     */
    @AutoLog(value = "资产标签-添加")
    @Operation(summary = "资产标签-添加")
    @RequiresPermissions("testnet.server:asset_label:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetLabel assetLabel) {
        assetLabelService.save(assetLabel);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param assetLabel
     * @return
     */
    @AutoLog(value = "资产标签-编辑")
    @Operation(summary = "资产标签-编辑")
    @RequiresPermissions("testnet.server:asset_label:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetLabel assetLabel) {
        assetLabelService.update(assetLabel);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "资产标签-通过id删除")
    @Operation(summary = "资产标签-通过id删除")
    @RequiresPermissions("testnet.server:asset_label:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        deleteLabel(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "资产标签-批量删除")
    @Operation(summary = "资产标签-批量删除")
    @RequiresPermissions("testnet.server:asset_label:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        for (String id : ids.split(",")) {
            deleteLabel(id);
        }
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "资产标签-通过id查询")
    @Operation(summary = "资产标签-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AssetLabel> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetLabel assetLabel = assetLabelService.getById(id);
        if (assetLabel == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(assetLabel);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetLabel
     */
    @RequiresPermissions("testnet.server:asset_label:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetLabel assetLabel) {
        return super.exportXlsSheet(request, assetLabel, AssetLabel.class, "资产标签", null, 50000);
        // return super.exportXls(request, assetLabel, AssetLabel.class, "资产标签");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_label:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AssetLabel.class);
    }

    private void deleteLabel(String id) {
        AssetLabel assetLabel = assetLabelService.getById(id);
        if (assetLabel != null) {
            assetLabelService.removeById(assetLabel);
            assetLabelService.cleanCache(assetLabel.getLabelName());
            assetLabelService.cleanCache(assetLabel.getId());
        }
    }
}
