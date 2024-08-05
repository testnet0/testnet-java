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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 任务关联资产表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("lite_flow_task_asset")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "lite_flow_task_asset对象", description = "任务关联资产表")
public class LiteFlowTaskAsset implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 资产id
     */
    @Excel(name = "资产id", width = 15)
    @ApiModelProperty(value = "资产id")
    private java.lang.String assetId;
    /**
     * 资产类型
     */
    @Excel(name = "资产类型", width = 15)
    @ApiModelProperty(value = "资产类型")
    private java.lang.String assetType;
    /**
     * 任务id
     */
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private java.lang.String liteFlowTaskId;
    /**
     * 任务版本
     */
    @Excel(name = "任务版本", width = 15)
    @ApiModelProperty(value = "任务版本")
    private java.lang.Integer version;
}
