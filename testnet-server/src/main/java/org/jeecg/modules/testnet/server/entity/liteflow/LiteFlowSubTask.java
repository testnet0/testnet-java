package org.jeecg.modules.testnet.server.entity.liteflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 子任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Schema(description="子任务表")
@Data
@TableName("lite_flow_sub_task")
public class LiteFlowSubTask implements Serializable {
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
     * 子任务参数
     */
    @Excel(name = "子任务参数", width = 15)
    @Schema(description = "子任务参数")
    private String subTaskParam;
    /**
     * 任务状态
     */
    @Excel(name = "任务状态", width = 15, dicCode = "plugin_status")
    @Dict(dicCode = "plugin_status")
    @Schema(description = "任务状态")
    private String taskStatus;
    /**
     * 执行节点
     */
    @Excel(name = "执行节点", width = 15, dictTable = "client", dicText = "client_name", dicCode = "id")
    @Dict(dictTable = "client", dicText = "client_name", dicCode = "id")
    @Schema(description = "执行节点")
    private String clientId;

    /**
     * 任务
     */
    @Schema(description = "任务")
    @Dict(dictTable = "lite_flow_task", dicText = "task_name", dicCode = "id")
    @Excel(name = "任务", width = 15, dictTable = "lite_flow_task", dicText = "task_name", dicCode = "id")
    private String taskId;
    /**
     * 配置
     */
    @Excel(name = "配置", width = 15)
    @Schema(description = "配置")
    private String config;

    @Schema(description = "版本")
    private Integer version;

    /**
     * Redis消息ID
     */
    @Excel(name = "Redis消息ID", width = 15)
    @Schema(description = "Redis消息ID")
    private java.lang.String redisMessageId;


}
