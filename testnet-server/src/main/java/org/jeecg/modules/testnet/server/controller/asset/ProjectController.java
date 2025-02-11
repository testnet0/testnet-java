package org.jeecg.modules.testnet.server.controller.asset;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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
import org.jeecg.modules.testnet.server.entity.asset.Project;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IProjectService;
import org.jeecg.modules.testnet.server.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 项目
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "项目")
@RestController
@RequestMapping("/testnet.server/project")
@Slf4j
public class ProjectController extends JeecgController<Project, IProjectService> {
    @Autowired
    private IProjectService projectService;

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    /**
     * 分页列表查询
     *
     * @param project
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "项目-分页列表查询")
    @ApiOperation(value = "项目-分页列表查询", notes = "项目-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ProjectVO>> queryPageList(Project project,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        QueryWrapper<Project> queryWrapper = QueryGenerator.initQueryWrapper(project, req.getParameterMap());
        Page<Project> page = new Page<Project>(pageNo, pageSize);
        IPage<Project> pageList = projectService.page(page, queryWrapper);
        List<ProjectVO> projectVOList = new ArrayList<>();
        pageList.getRecords().forEach(record -> {
            ProjectVO projectVO = new ProjectVO();
            BeanUtil.copyProperties(record, projectVO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            projectVO.setAssetIPCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.IP));
            projectVO.setAssetDomainCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.DOMAIN));
            projectVO.setAssetSubDomainCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.SUB_DOMAIN));
            projectVO.setAssetCompanyCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.COMPANY));
            projectVO.setAssetPortCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.PORT));
            projectVO.setAssetApiCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.API));
            projectVO.setAssetWebCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.WEB));
            projectVO.setAssetVulCount(assetCommonOptionService.getCountByProjectId(record.getId(), AssetTypeEnums.VUL));
            projectVOList.add(projectVO);
        });
        IPage<ProjectVO> pageVOList = new Page<>();
        BeanUtil.copyProperties(pageList, pageVOList, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        pageVOList.setRecords(projectVOList);
        return Result.OK(pageVOList);
    }

    /**
     * 添加
     *
     * @param project
     * @return
     */
    @AutoLog(value = "项目-添加")
    @ApiOperation(value = "项目-添加", notes = "项目-添加")
    @RequiresPermissions("testnet.server:project:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Project project) {
        projectService.save(project);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param project
     * @return
     */
    @AutoLog(value = "项目-编辑")
    @ApiOperation(value = "项目-编辑", notes = "项目-编辑")
    @RequiresPermissions("testnet.server:project:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody Project project) {
        projectService.updateById(project);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "项目-通过id删除")
    @ApiOperation(value = "项目-通过id删除", notes = "项目-通过id删除")
    @RequiresPermissions("testnet.server:project:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        delByProjectId(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "项目-批量删除")
    @ApiOperation(value = "项目-批量删除", notes = "项目-批量删除")
    @RequiresPermissions("testnet.server:project:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        for (String id : ids.split(",")) {
            delByProjectId(id);
        }
        return Result.OK("批量删除成功!");
    }

    private void delByProjectId(String id) {
        Project project = projectService.getById(id);
        if (project != null) {
            projectService.removeById(id);
            projectService.cleanCache(project.getId());
            projectService.cleanCache(project.getProjectName());
            for (AssetTypeEnums value : AssetTypeEnums.values()) {
                assetCommonOptionService.delAssetByProjectId(id, value);
            }
        }
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "项目-通过id查询")
    @ApiOperation(value = "项目-通过id查询", notes = "项目-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Project> queryById(@RequestParam(name = "id", required = true) String id) {
        Project project = projectService.getById(id);
        if (project == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(project);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param project
     */
    @RequiresPermissions("testnet.server:project:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Project project) {
        return super.exportXlsSheet(request, project, Project.class, "项目", null, 50000);
        // return super.exportXls(request, project, Project.class, "项目");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:project:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Project.class);
    }

}
