package org.jeecg.modules.testnet.server.controller.liteflow;

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
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 节点
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "节点")
@RestController
@RequestMapping("/testnet.server/client")
@Slf4j
public class ClientController extends JeecgController<Client, IClientService> {
    @Autowired
    private IClientService clientService;

    /**
     * 分页列表查询
     *
     * @param client
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "节点-分页列表查询")
    @Operation(summary = "节点-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Client>> queryPageList(Client client,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        QueryWrapper<Client> queryWrapper = QueryGenerator.initQueryWrapper(client, req.getParameterMap());
        Page<Client> page = new Page<Client>(pageNo, pageSize);
        IPage<Client> pageList = clientService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param client
     * @return
     */
    @AutoLog(value = "节点-添加")
    @Operation(summary = "节点-添加")
    @RequiresPermissions("testnet.server:client:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Client client) {
        clientService.save(client);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param client
     * @return
     */
    @AutoLog(value = "节点-编辑")
    @Operation(summary = "节点-编辑")
    @RequiresPermissions("testnet.server:client:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Client client) {
        clientService.updateById(client);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "节点-通过id删除")
    @Operation(summary = "节点-通过id删除")
    @RequiresPermissions("testnet.server:client:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        clientService.del(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "节点-批量删除")
    @Operation(summary = "节点-批量删除")
    @RequiresPermissions("testnet.server:client:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.clientService.del(ids);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "节点-通过id查询")
    @Operation(summary = "节点-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Client> queryById(@RequestParam(name = "id", required = true) String id) {
        Client client = clientService.getById(id);
        if (client == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(client);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param client
     */
    @RequiresPermissions("testnet.server:client:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Client client) {
        return super.exportXls(request, client, Client.class, "节点");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:client:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Client.class);
    }

}
