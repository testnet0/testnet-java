package org.jeecg.modules.testnet.server.entity.client;

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
 * @Description: 节点
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("client")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "client对象", description = "节点")
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**
     * 节点名称
     */
    @Excel(name = "节点名称", width = 15)
    @ApiModelProperty(value = "节点名称")
    private java.lang.String clientName;
    /**
     * 节点版本
     */
    @Excel(name = "节点版本", width = 15)
    @ApiModelProperty(value = "节点版本")
    private java.lang.String clientVersion;
    /**
     * 节点状态
     */
    @Excel(name = "节点状态", width = 15)
    @ApiModelProperty(value = "节点状态")
    private java.lang.String status;
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
     * 最后上报时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后上报时间")
    private java.util.Date updateTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;

    /**
     * CPU使用率
     */
    @ApiModelProperty(value = "CPU使用率")
    private java.lang.Double cpuUsage;

    @ApiModelProperty(value = "总内存")
    private java.lang.Integer totalMemory;

    @ApiModelProperty(value = "可用内存")
    private java.lang.Integer freeMemory;

}
