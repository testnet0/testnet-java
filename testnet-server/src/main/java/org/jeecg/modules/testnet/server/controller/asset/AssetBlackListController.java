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
import org.jeecg.modules.testnet.server.entity.asset.AssetBlackList;
import org.jeecg.modules.testnet.server.service.asset.IAssetBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 黑名单
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "黑名单")
@RestController
@RequestMapping("/testnet.server/blackList")
@Slf4j
public class AssetBlackListController extends JeecgController<AssetBlackList, IAssetBlackListService> {
    @Autowired
    private IAssetBlackListService blackListService;

    /**
     * 分页列表查询
     *
     * @param blackList
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "黑名单-分页列表查询")
    @ApiOperation(value = "黑名单-分页列表查询", notes = "黑名单-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AssetBlackList>> queryPageList(AssetBlackList blackList,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        QueryWrapper<AssetBlackList> queryWrapper = QueryGenerator.initQueryWrapper(blackList, req.getParameterMap());
        Page<AssetBlackList> page = new Page<AssetBlackList>(pageNo, pageSize);
        IPage<AssetBlackList> pageList = blackListService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param blackList
     * @return
     */
    @AutoLog(value = "黑名单-添加")
    @ApiOperation(value = "黑名单-添加", notes = "黑名单-添加")
    @RequiresPermissions("testnet.server:black_list:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetBlackList blackList) {
        blackListService.save(blackList);
        blackListService.clearCache();
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param blackList
     * @return
     */
    @AutoLog(value = "黑名单-编辑")
    @ApiOperation(value = "黑名单-编辑", notes = "黑名单-编辑")
    @RequiresPermissions("testnet.server:black_list:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetBlackList blackList) {
        blackListService.updateById(blackList);
        blackListService.clearCache();
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "黑名单-通过id删除")
    @ApiOperation(value = "黑名单-通过id删除", notes = "黑名单-通过id删除")
    @RequiresPermissions("testnet.server:black_list:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        blackListService.removeById(id);
        blackListService.clearCache();
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "黑名单-批量删除")
    @ApiOperation(value = "黑名单-批量删除", notes = "黑名单-批量删除")
    @RequiresPermissions("testnet.server:black_list:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.blackListService.removeByIds(Arrays.asList(ids.split(",")));
        blackListService.clearCache();
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "黑名单-通过id查询")
    @ApiOperation(value = "黑名单-通过id查询", notes = "黑名单-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AssetBlackList> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetBlackList blackList = blackListService.getById(id);
        if (blackList == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(blackList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param blackList
     */
    @RequiresPermissions("testnet.server:black_list:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetBlackList blackList) {
        return super.exportXlsSheet(request, blackList, AssetBlackList.class, "黑名单", null, 50000);
        // return super.exportXls(request, blackList, AssetBlackList.class, "黑名单");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:black_list:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AssetBlackList.class);
    }

}
