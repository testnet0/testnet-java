package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.testnet.server.entity.asset.AssetSearchEngine;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 空间引擎配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "空间引擎配置")
@RestController
@RequestMapping("/testnet/assetSearchEngine")
@Slf4j
public class SearchEngineConfigController extends JeecgController<AssetSearchEngine, IAssetSearchEngineService> {
    @Autowired
    private IAssetSearchEngineService assetSearchEngineService;

    /**
     * 分页列表查询
     *
     * @param assetSearchEngine
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "空间引擎配置-分页列表查询")
    @ApiOperation(value = "空间引擎配置-分页列表查询", notes = "空间引擎配置-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AssetSearchEngine>> queryPageList(AssetSearchEngine assetSearchEngine,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest req) {
        QueryWrapper<AssetSearchEngine> queryWrapper = QueryGenerator.initQueryWrapper(assetSearchEngine, req.getParameterMap());
        Page<AssetSearchEngine> page = new Page<AssetSearchEngine>(pageNo, pageSize);
        IPage<AssetSearchEngine> pageList = assetSearchEngineService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 编辑
     *
     * @param assetSearchEngine
     * @return
     */
    @AutoLog(value = "空间引擎配置-编辑")
    @ApiOperation(value = "空间引擎配置-编辑", notes = "空间引擎配置-编辑")
    @RequiresPermissions("testnet:asset_search_engine:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetSearchEngine assetSearchEngine) {
        assetSearchEngineService.updateById(assetSearchEngine);
        return Result.OK("编辑成功!");
    }


    /**
     * 导出excel
     *
     * @param request
     * @param assetSearchEngine
     */
    @RequiresPermissions("testnet:asset_search_engine:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetSearchEngine assetSearchEngine) {
        return super.exportXls(request, assetSearchEngine, AssetSearchEngine.class, "空间引擎配置");
    }
}
