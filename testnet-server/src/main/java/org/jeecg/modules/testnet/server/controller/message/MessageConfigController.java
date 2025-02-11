package org.jeecg.modules.testnet.server.controller.message;

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
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.system.message.MessageConfig;
import org.jeecg.modules.system.service.message.IMessageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 消息推送配置
 * @Author: jeecg-boot
 * @Date: 2024-09-27
 * @Version: V1.0
 */
@Api(tags = "消息推送配置")
@RestController
@RequestMapping("/iotaa/messageConfig")
@Slf4j
public class MessageConfigController extends JeecgController<MessageConfig, IMessageConfigService> {
    @Autowired
    private IMessageConfigService messageConfigService;

    /**
     * 分页列表查询
     *
     * @param messageConfig
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "消息推送配置-分页列表查询")
    @ApiOperation(value = "消息推送配置-分页列表查询", notes = "消息推送配置-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<MessageConfig>> queryPageList(MessageConfig messageConfig,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("messageType", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<MessageConfig> queryWrapper = QueryGenerator.initQueryWrapper(messageConfig, req.getParameterMap(), customeRuleMap);
        Page<MessageConfig> page = new Page<MessageConfig>(pageNo, pageSize);
        IPage<MessageConfig> pageList = messageConfigService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param messageConfig
     * @return
     */
    @AutoLog(value = "消息推送配置-添加")
    @ApiOperation(value = "消息推送配置-添加", notes = "消息推送配置-添加")
    @RequiresPermissions("iotaa:message_config:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody MessageConfig messageConfig) {
        messageConfigService.save(messageConfig);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param messageConfig
     * @return
     */
    @AutoLog(value = "消息推送配置-编辑")
    @ApiOperation(value = "消息推送配置-编辑", notes = "消息推送配置-编辑")
    @RequiresPermissions("iotaa:message_config:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody MessageConfig messageConfig) {
        messageConfigService.updateById(messageConfig);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "消息推送配置-通过id删除")
    @ApiOperation(value = "消息推送配置-通过id删除", notes = "消息推送配置-通过id删除")
    @RequiresPermissions("iotaa:message_config:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        messageConfigService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "消息推送配置-批量删除")
    @ApiOperation(value = "消息推送配置-批量删除", notes = "消息推送配置-批量删除")
    @RequiresPermissions("iotaa:message_config:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.messageConfigService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "消息推送配置-通过id查询")
    @ApiOperation(value = "消息推送配置-通过id查询", notes = "消息推送配置-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<MessageConfig> queryById(@RequestParam(name = "id", required = true) String id) {
        MessageConfig messageConfig = messageConfigService.getById(id);
        if (messageConfig == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(messageConfig);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param messageConfig
     */
    @RequiresPermissions("iotaa:message_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MessageConfig messageConfig) {
        return super.exportXls(request, messageConfig, MessageConfig.class, "消息推送配置");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("iotaa:message_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, MessageConfig.class);
    }

}
