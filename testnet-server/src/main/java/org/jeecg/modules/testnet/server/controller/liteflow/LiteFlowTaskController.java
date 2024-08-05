package org.jeecg.modules.testnet.server.controller.liteflow;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowTask;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskAssetService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowTaskService;
import org.jeecg.modules.testnet.server.vo.LiteFlowTaskPage;
import org.jeecg.modules.testnet.server.vo.LiteflowInstanceLogVO;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 扫描任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "扫描任务表")
@RestController
@RequestMapping("/testnet/liteFlowTask")
@Slf4j
public class LiteFlowTaskController {
    @Autowired
    private ILiteFlowTaskService liteFlowTaskService;
    @Autowired
    private ILiteFlowSubTaskService liteFlowSubTaskService;

    @Resource
    private ILiteFlowTaskAssetService liteFlowTaskAssetService;


    /**
     * 分页列表查询
     *
     * @param liteFlowTask
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "扫描任务表-分页列表查询")
    @ApiOperation(value = "扫描任务表-分页列表查询", notes = "扫描任务表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<LiteFlowTask>> queryPageList(LiteFlowTask liteFlowTask,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<LiteFlowTask> queryWrapper = QueryGenerator.initQueryWrapper(liteFlowTask, req.getParameterMap());
        Page<LiteFlowTask> page = new Page<LiteFlowTask>(pageNo, pageSize);
        IPage<LiteFlowTask> pageList = liteFlowTaskService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    @ApiOperation(value = "子任务表-分页列表查询", notes = "子任务表-分页列表查询")
    @GetMapping(value = "/subTaskList")
    public Result<IPage<LiteFlowSubTask>> subTaskList(LiteFlowSubTask liteFlowSubTask, @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<LiteFlowSubTask> queryWrapper = QueryGenerator.initQueryWrapper(liteFlowSubTask, req.getParameterMap());
        Page<LiteFlowSubTask> page = new Page<>(pageNo, pageSize);
        IPage<LiteFlowSubTask> pageList = liteFlowSubTaskService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 编辑
     *
     * @param liteFlowTask
     * @return
     */
    @AutoLog(value = "扫描任务表-编辑")
    @ApiOperation(value = "扫描任务表-编辑", notes = "扫描任务表-编辑")
    @RequiresPermissions("testnet:lite_flow_task:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody LiteFlowTask liteFlowTask) {

        return liteFlowTaskService.edit(liteFlowTask);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "扫描任务表-通过id删除")
    @ApiOperation(value = "扫描任务表-通过id删除", notes = "扫描任务表-通过id删除")
    @RequiresPermissions("testnet:lite_flow_task:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        liteFlowTaskService.delJob(id);
        liteFlowTaskService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "扫描任务表-通过id删除")
    @ApiOperation(value = "扫描任务表-通过id删除", notes = "扫描任务表-通过id删除")
    @RequiresPermissions("testnet:lite_flow_task:delete")
    @DeleteMapping(value = "/deleteByTask")
    public Result<String> deleteByTask(@RequestParam(name = "id", required = true) String id) {
        liteFlowTaskAssetService.deleteByTask(id);
        return Result.OK("删除成功!");
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "扫描任务表-批量删除")
    @ApiOperation(value = "扫描任务表-批量删除", notes = "扫描任务表-批量删除")
    @RequiresPermissions("testnet:lite_flow_task:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.liteFlowTaskService.delBatchMain(Arrays.asList(ids.split(",")));
        for (String id : ids.split(",")) {
            liteFlowTaskService.delJob(id);
        }
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "扫描任务表-通过id查询")
    @ApiOperation(value = "扫描任务表-通过id查询", notes = "扫描任务表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<LiteFlowTask> queryById(@RequestParam(name = "id", required = true) String id) {
        LiteFlowTask liteFlowTask = liteFlowTaskService.getById(id);
        if (liteFlowTask == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(liteFlowTask);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "子任务表-通过主表ID查询")
    @ApiOperation(value = "子任务表-通过主表ID查询", notes = "子任务表-通过主表ID查询")
    @GetMapping(value = "/queryLiteFlowSubTaskByMainId")
    public Result<IPage<LiteFlowSubTask>> queryLiteFlowSubTaskListByMainId(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<LiteFlowSubTask> page = liteFlowSubTaskService.selectByMainId(id, pageNum, pageSize);
        return Result.OK(page);
    }


    /**
     * 导出excel
     *
     * @param request
     * @param liteFlowTask
     */
    @RequiresPermissions("testnet:lite_flow_task:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LiteFlowTask liteFlowTask) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<LiteFlowTask> queryWrapper = QueryGenerator.initQueryWrapper(liteFlowTask, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //Step.2 获取导出数据
        List<LiteFlowTask> liteFlowTaskList = liteFlowTaskService.list(queryWrapper);

        // Step.3 组装pageList
        List<LiteFlowTaskPage> pageList = new ArrayList<LiteFlowTaskPage>();
        for (LiteFlowTask main : liteFlowTaskList) {
            LiteFlowTaskPage vo = new LiteFlowTaskPage();
            BeanUtils.copyProperties(main, vo);
            List<LiteFlowSubTask> liteFlowSubTaskList = liteFlowSubTaskService.selectByMainId(main.getId());
            vo.setLiteFlowSubTaskList(liteFlowSubTaskList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "扫描任务表列表");
        mv.addObject(NormalExcelConstants.CLASS, LiteFlowTaskPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("扫描任务表数据", "导出人:" + sysUser.getRealname(), "扫描任务表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "工作流实例-通过ID查询日志")
    @ApiOperation(value = "工作流实例-通过ID查询日志", notes = "工作流实例-通过ID查询日志")
    @GetMapping(value = "/queryLogBySubTaskId")
    public Result<IPage<LiteflowInstanceLogVO>> queryLogByPluginInstanceId(@RequestParam(name = "id") String id,
                                                                           @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNo,
                                                                           @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize) {
        return Result.OK(liteFlowSubTaskService.getLogById(id, pageNo, pageSize));
    }


    @ApiOperation(value = "再次运行", notes = "再次运行")
    @GetMapping(value = "/executeAgain")
    public Result<String> executeAgain(@RequestParam(name = "id", required = true) String id) {
        liteFlowTaskService.executeAgain(id);
        return Result.ok("再次运行成功！");
    }

    @ApiOperation(value = "停止运行", notes = "停止运行")
    @GetMapping(value = "/stopTask")
    public Result<String> stopTask(@RequestParam(name = "id", required = true) String id) {
        return liteFlowTaskService.stopTask(id);

    }

    @ApiOperation(value = "取消运行", notes = "停止运行")
    @DeleteMapping(value = "/cancelSubTask")
    public Result<String> cancelSubTask(@RequestParam(name = "ids", required = true) String ids) {
        liteFlowSubTaskService.cancelSubTask(ids);
        return Result.ok("取消运行成功！");
    }

    /**
     * 更新状态
     */
    @AutoLog(value = "任务管理-更新定时任务状态")
    @ApiOperation(value = "任务管理-更新定时任务状态", notes = "任务管理-更新定时任务状态")
    @RequiresPermissions("testnet:lite_flow_task:edit")
    @GetMapping(value = "/changeCronStatus")
    public Result<String> changeCronStatus(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "status", required = true) Boolean status) {

        return liteFlowTaskService.changeCronStatus(id, status);
    }

}
