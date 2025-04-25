package org.jeecg.modules.testnet.server.entity.liteflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 脚本
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("lite_flow_script")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="脚本")
public class Script implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    private String sysOrgCode;
    /**
     * 脚本名称
     */
    @Excel(name = "脚本ID", width = 15)
    @Schema(description = "脚本ID")
    private String scriptId;

    /**
     * 脚本名称
     */
    @Excel(name = "脚本名称", width = 15)
    @Schema(description = "脚本名称")
    private String scriptName;
    /**
     * 脚本类型
     */
    @Excel(name = "脚本类型", width = 15, dicCode = "script_type")
    @Dict(dicCode = "script_type")
    @Schema(description = "脚本类型")
    private String scriptType;
    /**
     * 脚本内容
     */
    @Excel(name = "脚本内容", width = 15)
    @Schema(description = "脚本内容")
    private String scriptData;
    /**
     * 脚本语言
     */
    @Excel(name = "脚本语言", width = 15, dicCode = "script_language")
    @Dict(dicCode = "script_language")
    @Schema(description = "脚本语言")
    private String scriptLanguage;
    /**
     * 应用名
     */
    @Excel(name = "应用名", width = 15)
    @Schema(description = "应用名")
    private String applicationName;

    /**
     * 启用
     */
    @Excel(name = "启用", width = 15)
    @Schema(description = "启用")
    private Boolean enable;

    /**
     * 脚本分类
     */
    @Excel(name = "脚本分类", width = 15, dicCode = "script_dict")
    @Dict(dicCode = "script_dict")
    @Schema(description = "脚本分类")
    private java.lang.String scriptDict;

}
