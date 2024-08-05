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
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.system.service.ISysDataLogService;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.processer.impl.AssetResultProcessorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 流程管理
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "流程管理")
@RestController
@RequestMapping("/testnet.server/chain")
@Slf4j
public class ChainController extends JeecgController<Chain, IChainService> {
    @Autowired
    private IChainService chainService;
    @Resource
    private AssetResultProcessorServiceImpl assetResultProcessorService;
    @Resource
    private ISysDataLogService sysDataLogService;

    @Resource
    private IClientConfigService clientConfigService;

    /**
     * 分页列表查询
     *
     * @param chain
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "流程管理-分页列表查询")
    @ApiOperation(value = "流程管理-分页列表查询", notes = "流程管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Chain>> queryPageList(Chain chain,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {
        QueryWrapper<Chain> queryWrapper = QueryGenerator.initQueryWrapper(chain, req.getParameterMap());
        Page<Chain> page = new Page<Chain>(pageNo, pageSize);
        IPage<Chain> pageList = chainService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param chain
     * @return
     */
    @AutoLog(value = "流程管理-添加")
    @ApiOperation(value = "流程管理-添加", notes = "流程管理-添加")
    @RequiresPermissions("testnet.server:chain:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Chain chain) {
        chainService.save(chain);
        clientConfigService.addConfig(chain);
        chainService.clearCache();
        return Result.OK("添加成功！");
    }

    /**
     * 复制
     *
     * @param id
     * @return
     */
    @AutoLog(value = "流程管理-复制")
    @ApiOperation(value = "流程管理-复制", notes = "流程管理-复制")
    @RequiresPermissions("testnet.server:chain:add")
    @GetMapping(value = "/copyChain")
    public Result<String> copyChain(@RequestParam(name = "id", required = true) String id) {
        chainService.copyChain(id);
        chainService.clearCache();
        return Result.OK("添加成功！");
    }

    /**
     * 更新状态
     */
    @AutoLog(value = "流程管理-更新状态")
    @ApiOperation(value = "流程管理-更新状态", notes = "流程管理-更新状态")
    @RequiresPermissions("testnet.server:chain:edit")
    @GetMapping(value = "/changeStatus")
    public Result<String> changeStatus(@RequestParam(name = "field", required = true) String field, @RequestParam(name = "id", required = true) String id, @RequestParam(name = "status", required = true) Boolean status) {
        chainService.changeStatus(field, id, status);
        chainService.clearCache();
        return Result.OK("更新成功！");
    }

    /**
     * 编辑
     *
     * @param chain
     * @return
     */
    @AutoLog(value = "流程管理-编辑")
    @ApiOperation(value = "流程管理-编辑", notes = "流程管理-编辑")
    @RequiresPermissions("testnet.server:chain:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Chain chain) {
        sysDataLogService.addDataLog("lite_flow_chain", chain.getId(), chain.getElData());
        chainService.updateById(chain);
        chainService.clearCache();
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "流程管理-通过id删除")
    @ApiOperation(value = "流程管理-通过id删除", notes = "流程管理-通过id删除")
    @RequiresPermissions("testnet.server:chain:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        chainService.removeById(id);
        clientConfigService.delByChainIds(Collections.singletonList(id));
        chainService.clearCache();
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "流程管理-批量删除")
    @ApiOperation(value = "流程管理-批量删除", notes = "流程管理-批量删除")
    @RequiresPermissions("testnet.server:chain:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.chainService.removeByIds(Arrays.asList(ids.split(",")));
        clientConfigService.delByChainIds(Arrays.asList(ids.split(",")));
        chainService.clearCache();
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "流程管理-通过id查询")
    @ApiOperation(value = "流程管理-通过id查询", notes = "流程管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Chain> queryById(@RequestParam(name = "id", required = true) String id) {
        Chain chain = chainService.getById(id);
        if (chain == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(chain);
    }

    /**
     * 通过资产类型查询
     *
     * @param assetType
     * @return
     */
    @ApiOperation(value = "流程管理-通过资产类型查询", notes = "流程管理-通过资产类型查")
    @GetMapping(value = "/queryByAssetType")
    @RequiresPermissions("testnet.server:chain:queryByAssetType")
    public Result<List<Chain>> queryByAssetType(@RequestParam(name = "assetType") String assetType) {
        List<Chain> chain = chainService.getChainListByAssetType(assetType);
        return Result.OK(chain);
    }


    /**
     * 导出excel
     *
     * @param request
     * @param chain
     */
    @RequiresPermissions("testnet.server:chain:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Chain chain) {
        return super.exportXls(request, chain, Chain.class, "流程管理");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:chain:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Chain.class);
    }


    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "流程管理-查询所有的处理器")
    @ApiOperation(value = "流程管理-查询所有的处理器", notes = "流程管理-查询所有的处理器")
    @GetMapping(value = "/getProcessList")
    public Result<List<DictModel>> getProcessList() {
        List<DictModel> chain = assetResultProcessorService.getProcessList();
        return Result.OK(chain);
    }

}
