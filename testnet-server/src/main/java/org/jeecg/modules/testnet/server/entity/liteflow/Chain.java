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
 * @Description: 流程管理
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("lite_flow_chain")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "chain对象", description = "流程管理")
public class Chain implements Serializable {
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
     * 流程名称
     */
    @Excel(name = "流程名称", width = 15)
    @ApiModelProperty(value = "流程名称")
    private String chainName;
    /**
     * EL表达式
     */
    @Excel(name = "EL表达式", width = 15)
    @ApiModelProperty(value = "EL表达式")
    private String elData;
    /**
     * 应用名
     */
    @Excel(name = "应用名", width = 15)
    @ApiModelProperty(value = "应用名")
    private String applicationName;

    /**
     * 图标
     */
    @Excel(name = "图标", width = 15)
    @ApiModelProperty(value = "图标")
    private String icon;

    /**
     * 启用
     */
    @Excel(name = "启用", width = 15)
    @ApiModelProperty(value = "启用")
    private Boolean enable;

    /**
     * 适用资产
     */
    @Excel(name = "适用资产", width = 15, dicCode = "asset_type")
    @Dict(dicCode = "asset_type")
    @ApiModelProperty(value = "适用资产")
    private java.lang.String assetType;

    /**
     * 默认参数
     */
    @Excel(name = "默认配置", width = 15)
    @ApiModelProperty(value = "默认配置")
    private java.lang.String config;

    /**
     * 处理结果类名
     */
    @Excel(name = "处理结果类名", width = 15)
    @ApiModelProperty(value = "处理结果类名")
    private java.lang.String processorClassName;

    /**
     * 默认线程
     */
    @Excel(name = "默认线程", width = 15)
    @ApiModelProperty(value = "默认线程")
    private java.lang.Integer defaultThread;

}