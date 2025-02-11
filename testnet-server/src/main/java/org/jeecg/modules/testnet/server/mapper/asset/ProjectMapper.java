package org.jeecg.modules.testnet.server.mapper.asset;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.testnet.server.entity.asset.Project;

/**
 * @Description: 项目
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 根据项目id或项目名称查询项目
     *
     * @param s
     * @return
     */
    @Select("select * from project where id = #{s} or project_name = #{s}")
    Project getProjectByIdOrName(String s);


}
