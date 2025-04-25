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
 * @Description: 扫描任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Schema(description="扫描任务表")
@Data
@TableName("lite_flow_task")
public class LiteFlowTask implements Serializable {
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
     * 扫描任务名称
     */
    @Excel(name = "扫描任务名称", width = 15)
    @Schema(description = "扫描任务名称")
    private String taskName;
    /**
     * 查询参数
     */
    @Excel(name = "查询参数", width = 15)
    @Schema(description = "查询参数")
    private String searchParam;
    /**
     * 资产类型
     */
    @Excel(name = "资产类型", width = 15, dicCode = "asset_type")
    @Dict(dicCode = "asset_type")
    @Schema(description = "资产类型")
    private String assetType;
    /**
     * 资产数量
     */
    @Excel(name = "资产数量", width = 15)
    @Schema(description = "资产数量")
    private Integer assetNum;
    /**
     * 定时执行
     */
    @Excel(name = "定时执行", width = 15)
    @Schema(description = "定时执行")
    private Integer isCron;
    /**
     * CRON表达式
     */
    @Excel(name = "CRON表达式", width = 15)
    @Schema(description = "CRON表达式")
    private String jobCron;
    /**
     * 执行通知
     */
    @Excel(name = "执行通知", width = 15)
    @Schema(description = "执行通知")
    private Integer isAlarm;
    /**
     * 流程
     */
    @Excel(name = "流程", width = 15, dictTable = "lite_flow_chain", dicText = "chain_name", dicCode = "id")
    @Dict(dictTable = "lite_flow_chain", dicText = "chain_name", dicCode = "id")
    @Schema(description = "流程")
    private String chainId;

    @Schema(description = "版本")
    private Integer version;

    @Schema(description = "未完成数量")
    private Integer unFinishedChain;

    @Schema(description = "优先级")
    private Integer priority;

    /**
     * 路由策略
     */
    @Excel(name = "路由策略", width = 15, dicCode = "router")
    @Dict(dicCode = "router")
    @Schema(description = "路由策略")
    private java.lang.String router;

    /**
     * 节点
     */
    @Excel(name = "节点", width = 15)
    @Schema(description = "节点")
    @Dict(dictTable = "client", dicText = "client_name", dicCode = "id")
    private String clientId;


    private String quartzJobId;
}
