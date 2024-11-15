package org.jeecg.modules.testnet.server.controller.liteflow;

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
import org.jeecg.modules.testnet.server.entity.liteflow.Script;
import org.jeecg.modules.testnet.server.service.client.IClientToolsService;
import org.jeecg.modules.testnet.server.service.liteflow.IScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 脚本
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "脚本")
@RestController
@RequestMapping("/testnet.server/script")
@Slf4j
public class ScriptController extends JeecgController<Script, IScriptService> {
    @Autowired
    private IScriptService scriptService;

    @Resource
    private IClientToolsService clientToolsService;

    /**
     * 分页列表查询
     *
     * @param script
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "脚本-分页列表查询")
    @ApiOperation(value = "脚本-分页列表查询", notes = "脚本-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Script>> queryPageList(Script script,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        QueryWrapper<Script> queryWrapper = QueryGenerator.initQueryWrapper(script, req.getParameterMap());
        queryWrapper.ne("script_dict", "SYSTEM");
        Page<Script> page = new Page<Script>(pageNo, pageSize);
        IPage<Script> pageList = scriptService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param script
     * @return
     */
    @AutoLog(value = "脚本-添加")
    @ApiOperation(value = "脚本-添加", notes = "脚本-添加")
    @RequiresPermissions("testnet.server:script:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Script script) {
        scriptService.saveScript(script);
        if (script.getNeedInstall()) {
            clientToolsService.addConfig(script);
        }
        return Result.OK("添加成功！");
    }

    /**
     * 脚本-复制
     *
     * @param id
     * @return
     */
    @AutoLog(value = "脚本-通过id复制")
    @ApiOperation(value = "脚本-通过id复制", notes = "脚本-通过id复制")
    @RequiresPermissions("testnet.server:script:add")
    @GetMapping(value = "/copyScript")
    public Result<String> copyScript(@RequestParam(name = "id", required = true) String id) {
        scriptService.copyScript(id);
        return Result.OK("复制成功!");
    }


    /**
     * 编辑
     *
     * @param script
     * @return
     */
    @AutoLog(value = "脚本-编辑")
    @ApiOperation(value = "脚本-编辑", notes = "脚本-编辑")
    @RequiresPermissions("testnet.server:script:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Script script) {
        scriptService.updateScript(script);
        return Result.OK("编辑成功!");
    }

    /**
     * 更新状态
     */
    @AutoLog(value = "脚本管理-更新状态")
    @ApiOperation(value = "脚本管理-更新状态", notes = "脚本管理-更新状态")
    @RequiresPermissions("testnet.server:script:edit")
    @GetMapping(value = "/changeStatus")
    public Result<String> changeStatus(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "status", required = true) Boolean status) {
        scriptService.changeStatus(id, status);
        return Result.OK("更新成功！");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "脚本-通过id删除")
    @ApiOperation(value = "脚本-通过id删除", notes = "脚本-通过id删除")
    @RequiresPermissions("testnet.server:script:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        scriptService.deleteScript(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "脚本-批量删除")
    @ApiOperation(value = "脚本-批量删除", notes = "脚本-批量删除")
    @RequiresPermissions("testnet.server:script:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        for (String s : ids.split(",")) {
            scriptService.deleteScript(s);
        }
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "脚本-通过id查询")
    @ApiOperation(value = "脚本-通过id查询", notes = "脚本-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Script> queryById(@RequestParam(name = "id", required = true) String id) {
        Script script = scriptService.getById(id);
        if (script == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(script);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param script
     */
    @RequiresPermissions("testnet.server:script:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Script script) {
        return super.exportXls(request, script, Script.class, "脚本");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:script:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Script.class);
    }

}
