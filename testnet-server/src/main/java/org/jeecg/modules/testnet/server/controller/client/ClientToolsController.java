package org.jeecg.modules.testnet.server.controller.client;

import com.alibaba.fastjson.JSONObject;
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
import org.jeecg.modules.testnet.server.entity.client.ClientTools;
import org.jeecg.modules.testnet.server.service.client.IClientToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 节点工具
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "节点工具")
@RestController
@RequestMapping("/cn/clientTools")
@Slf4j
public class ClientToolsController extends JeecgController<ClientTools, IClientToolsService> {
    @Autowired
    private IClientToolsService clientToolsService;

    /**
     * 分页列表查询
     *
     * @param clientTools
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "节点工具-分页列表查询")
    @ApiOperation(value = "节点工具-分页列表查询", notes = "节点工具-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ClientTools>> queryPageList(ClientTools clientTools,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<ClientTools> queryWrapper = QueryGenerator.initQueryWrapper(clientTools, req.getParameterMap());
        Page<ClientTools> page = new Page<ClientTools>(pageNo, pageSize);
        IPage<ClientTools> pageList = clientToolsService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param clientTools
     * @return
     */
    @AutoLog(value = "节点工具-添加")
    @ApiOperation(value = "节点工具-添加", notes = "节点工具-添加")
    @RequiresPermissions("cn:client_tools:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ClientTools clientTools) {
        clientToolsService.save(clientTools);
        return Result.OK("添加成功！");
    }

    @AutoLog(value = "节点工具-修改状态")
    @ApiOperation(value = "节点工具-修改状态", notes = "节点工具-修改状态")
    @RequiresPermissions("cn:client_tools:edit")
    @GetMapping(value = "/changeToolStatus")
    public Result<String> changeToolStatus(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "status", required = true) Boolean status) {
        clientToolsService.changeStatus(id, status);
        return Result.OK("更新成功！");
    }


    /**
     * 编辑
     *
     * @param clientTools
     * @return
     */
    @AutoLog(value = "节点工具-编辑")
    @ApiOperation(value = "节点工具-编辑", notes = "节点工具-编辑")
    @RequiresPermissions("cn:client_tools:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody ClientTools clientTools) {
        clientToolsService.updateById(clientTools);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "节点工具-通过id删除")
    @ApiOperation(value = "节点工具-通过id删除", notes = "节点工具-通过id删除")
    @RequiresPermissions("cn:client_tools:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        clientToolsService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "节点工具-批量删除")
    @ApiOperation(value = "节点工具-批量删除", notes = "节点工具-批量删除")
    @RequiresPermissions("cn:client_tools:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.clientToolsService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "节点工具-通过id查询")
    @ApiOperation(value = "节点工具-通过id查询", notes = "节点工具-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ClientTools> queryById(@RequestParam(name = "id", required = true) String id) {
        ClientTools clientTools = clientToolsService.getById(id);
        if (clientTools == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(clientTools);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param clientTools
     */
    @RequiresPermissions("cn:client_tools:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ClientTools clientTools) {
        return super.exportXls(request, clientTools, ClientTools.class, "节点工具");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("cn:client_tools:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ClientTools.class);
    }

    /**
     * 安装
     *
     * @param json
     * @return
     */
    @AutoLog(value = "节点工具-安装")
    @ApiOperation(value = "节点工具-添加", notes = "节点工具-添加")
    @RequiresPermissions("cn:client_tools:add")
    @PostMapping(value = "/installTools")
    public Result<String> installTools(@RequestBody JSONObject json) {
        clientToolsService.installTools(json.getJSONArray("ids"));
        return Result.OK("开始安装，请去工作流管理-任务列表查看进度！");
    }

}
