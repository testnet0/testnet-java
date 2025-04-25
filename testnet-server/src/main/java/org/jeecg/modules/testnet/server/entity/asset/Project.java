package org.jeecg.modules.testnet.server.entity.asset;

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

/**
 * @Description: 项目
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="项目")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private java.lang.String id;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private java.lang.String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 项目名称
     */
    @Excel(name = "项目名称", width = 15)
    @Schema(description = "项目名称")
    private java.lang.String projectName;
    /**
     * 项目地址
     */
    @Excel(name = "项目地址", width = 15)
    @Schema(description = "项目地址")
    private java.lang.String address;
    /**
     * 优先级
     */
    @Excel(name = "优先级", width = 15, dicCode = "priority")
    @Dict(dicCode = "priority")
    @Schema(description = "优先级")
    private java.lang.String level;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private java.lang.String commnet;

    /**
     * 微信公众号
     */
    @Excel(name = "微信公众号", width = 15)
    @Schema(description = "微信公众号")
    private java.lang.String wechat;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 15)
    @Schema(description = "邮箱")
    private java.lang.String mail;
    /**
     * 微博
     */
    @Excel(name = "微博", width = 15)
    @Schema(description = "微博")
    private java.lang.String weibo;
    /**
     * 微博链接
     */
    @Excel(name = "微博链接", width = 15)
    @Schema(description = "微博链接")
    private java.lang.String weiboLink;
}
