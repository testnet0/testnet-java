package org.jeecg.modules.testnet.server.service.asset.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.testnet.server.entity.asset.Project;
import org.jeecg.modules.testnet.server.mapper.asset.ProjectMapper;
import org.jeecg.modules.testnet.server.service.asset.IProjectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
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
    private ProjectMapper projectMapper;

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

    @Override
    @Cacheable(value = "asset:project:cache", key = "#projectIdOrName", unless = "#result == null ")
    public Project getByProjectIdOrName(String projectIdOrName) {
        return projectMapper.getProjectByIdOrName(projectIdOrName);
    }

    @CacheEvict(value = "asset:project:cache", key = "#projectIdOrName")
    public void cleanCache(String projectIdOrName) {
    }
}
