package org.jeecg.modules.testnet.server.service.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.entity.asset.Project;
import org.jeecg.modules.testnet.server.vo.ProjectVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: 项目
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IProjectService extends IService<Project> {

    IPage<ProjectVO> listByProject(Project project, HttpServletRequest req, Integer pageNo, Integer pageSize);

    void delById(String ids);

    long getCount(Date time);
}
