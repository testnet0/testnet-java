package org.jeecg.modules.testnet.server.service.asset.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.testnet.server.entity.asset.Project;
import org.jeecg.modules.testnet.server.mapper.asset.ProjectMapper;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IProjectService;
import org.jeecg.modules.testnet.server.vo.ProjectVO;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Description: 项目
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    @Override
    public IPage<ProjectVO> listByProject(Project project, HttpServletRequest req, Integer pageNo, Integer pageSize) {
        QueryWrapper<Project> queryWrapper = QueryGenerator.initQueryWrapper(project, req.getParameterMap());
        Page<Project> page = new Page<Project>(pageNo, pageSize);
        IPage<Project> pageList = page(page, queryWrapper);
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
        return pageVOList;
    }

    @Override
    public void delById(String ids) {
        for (String id : ids.split(",")) {
            removeById(id);
            for (AssetTypeEnums value : AssetTypeEnums.values()) {
                assetCommonOptionService.delAssetByProjectId(id, value);
            }
        }
    }

    @Override
    public long getCount(Date time) {
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.gt(Project::getCreateTime, time);
        return count(lambdaQueryWrapper);
    }

    private boolean isValid(Project project) {
        return StringUtils.isNotBlank(project.getProjectName());
    }

    @Override
    public boolean saveBatch(Collection<Project> projectCollection, int batchSize) {
        if (projectCollection == null || projectCollection.isEmpty()) {
            return false;
        }
        List<Project> assets = new ArrayList<>();
        projectCollection.forEach(project -> {
            if (isValid(project)) {
                assets.add(project);
            }
        });
        if (!assets.isEmpty()) {
            super.saveBatch(assets, 1000);
        }
        return true;
    }
}
