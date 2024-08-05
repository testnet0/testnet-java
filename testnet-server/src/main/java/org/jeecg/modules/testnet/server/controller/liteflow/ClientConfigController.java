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
import org.jeecg.modules.testnet.server.entity.client.ClientConfig;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 节点配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "节点配置")
@RestController
@RequestMapping("/testnet.server/clientConfig")
@Slf4j
public class ClientConfigController extends JeecgController<ClientConfig, IClientConfigService> {
    @Autowired
    private IClientConfigService clientConfigService;

    /**
     * 分页列表查询
     *
     * @param clientConfig
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "节点配置-分页列表查询")
    @ApiOperation(value = "节点配置-分页列表查询", notes = "节点配置-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ClientConfig>> queryPageList(ClientConfig clientConfig,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<ClientConfig> queryWrapper = QueryGenerator.initQueryWrapper(clientConfig, req.getParameterMap());
        Page<ClientConfig> page = new Page<ClientConfig>(pageNo, pageSize);
        IPage<ClientConfig> pageList = clientConfigService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param clientConfig
     * @return
     */
    @AutoLog(value = "节点配置-添加")
    @ApiOperation(value = "节点配置-添加", notes = "节点配置-添加")
    @RequiresPermissions("testnet.server:client_config:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ClientConfig clientConfig) {
        if (clientConfigService.getClientConfig(clientConfig.getClientId(), clientConfig.getChainId()) == null) {
            clientConfigService.save(clientConfig);
            return Result.OK("添加成功！");
        } else {
            return Result.error("该流程已配置，请勿重复配置！");
        }
    }

    /**
     * 编辑
     *
     * @param clientConfig
     * @return
     */
    @AutoLog(value = "节点配置-编辑")
    @ApiOperation(value = "节点配置-编辑", notes = "节点配置-编辑")
    @RequiresPermissions("testnet.server:client_config:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody ClientConfig clientConfig) {
        clientConfigService.updateById(clientConfig);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "节点配置-通过id删除")
    @ApiOperation(value = "节点配置-通过id删除", notes = "节点配置-通过id删除")
    @RequiresPermissions("testnet.server:client_config:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        clientConfigService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "节点配置-批量删除")
    @ApiOperation(value = "节点配置-批量删除", notes = "节点配置-批量删除")
    @RequiresPermissions("testnet.server:client_config:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.clientConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "节点配置-通过id查询")
    @ApiOperation(value = "节点配置-通过id查询", notes = "节点配置-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ClientConfig> queryById(@RequestParam(name = "id", required = true) String id) {
        ClientConfig clientConfig = clientConfigService.getById(id);
        if (clientConfig == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(clientConfig);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param clientConfig
     */
    @RequiresPermissions("testnet.server:client_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ClientConfig clientConfig) {
        return super.exportXls(request, clientConfig, ClientConfig.class, "节点配置");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:client_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ClientConfig.class);
    }

}
