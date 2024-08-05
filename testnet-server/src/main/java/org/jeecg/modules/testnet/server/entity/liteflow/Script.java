package org.jeecg.modules.testnet.server.entity.liteflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "script对象", description = "脚本")
public class Script implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 脚本名称
     */
    @Excel(name = "脚本ID", width = 15)
    @ApiModelProperty(value = "脚本ID")
    private String scriptId;

    /**
     * 脚本名称
     */
    @Excel(name = "脚本名称", width = 15)
    @ApiModelProperty(value = "脚本名称")
    private String scriptName;
    /**
     * 脚本类型
     */
    @Excel(name = "脚本类型", width = 15, dicCode = "script_type")
    @Dict(dicCode = "script_type")
    @ApiModelProperty(value = "脚本类型")
    private String scriptType;
    /**
     * 脚本内容
     */
    @Excel(name = "脚本内容", width = 15)
    @ApiModelProperty(value = "脚本内容")
    private String scriptData;
    /**
     * 脚本语言
     */
    @Excel(name = "脚本语言", width = 15, dicCode = "script_language")
    @Dict(dicCode = "script_language")
    @ApiModelProperty(value = "脚本语言")
    private String scriptLanguage;
    /**
     * 应用名
     */
    @Excel(name = "应用名", width = 15)
    @ApiModelProperty(value = "应用名")
    private String applicationName;

    /**
     * 启用
     */
    @Excel(name = "启用", width = 15)
    @ApiModelProperty(value = "启用")
    private Boolean enable;

    /**
     * 脚本分类
     */
    @Excel(name = "脚本分类", width = 15, dicCode = "script_dict")
    @Dict(dicCode = "script_dict")
    @ApiModelProperty(value = "脚本分类")
    private java.lang.String scriptDict;

    /**
     * 免安装
     */
    @Excel(name = "免安装", width = 15)
    @ApiModelProperty(value = "免安装")
    private Boolean needInstall;
    /**
     * 安装命令
     */
    @Excel(name = "安装命令", width = 15)
    @ApiModelProperty(value = "安装命令")
    private java.lang.String installCommand;
    /**
     * 版本检查命令
     */
    @Excel(name = "版本检查命令", width = 15)
    @ApiModelProperty(value = "版本检查命令")
    private java.lang.String versionCheckCommand;
    /**
     * 版本更新命令
     */
    @Excel(name = "版本更新命令", width = 15)
    @ApiModelProperty(value = "版本更新命令")
    private java.lang.String updateCommand;
}
