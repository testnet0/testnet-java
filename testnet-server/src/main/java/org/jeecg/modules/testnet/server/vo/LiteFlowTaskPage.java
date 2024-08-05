package org.jeecg.modules.testnet.server.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.testnet.server.entity.liteflow.LiteFlowSubTask;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 扫描任务表
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@ApiModel(value = "lite_flow_taskPage对象", description = "扫描任务表")
public class LiteFlowTaskPage {

    /**
     * 主键
     */
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
     * 扫描任务名称
     */
    @Excel(name = "扫描任务名称", width = 15)
    @ApiModelProperty(value = "扫描任务名称")
    private String taskName;
    /**
     * 查询参数
     */
    @Excel(name = "查询参数", width = 15)
    @ApiModelProperty(value = "查询参数")
    private String searchParam;
    /**
     * 资产类型
     */
    @Excel(name = "资产类型", width = 15, dicCode = "asset_type")
    @Dict(dicCode = "asset_type")
    @ApiModelProperty(value = "资产类型")
    private String assetType;
    /**
     * 资产数量
     */
    @Excel(name = "资产数量", width = 15)
    @ApiModelProperty(value = "资产数量")
    private Integer assetNum;
    /**
     * 定时执行
     */
    @Excel(name = "定时执行", width = 15)
    @ApiModelProperty(value = "定时执行")
    private Integer isCron;
    /**
     * CRON表达式
     */
    @Excel(name = "CRON表达式", width = 15)
    @ApiModelProperty(value = "CRON表达式")
    private String jobCron;
    /**
     * 执行通知
     */
    @Excel(name = "执行通知", width = 15)
    @ApiModelProperty(value = "执行通知")
    private Integer isAlarm;
    /**
     * 流程
     */
    @Excel(name = "流程", width = 15, dictTable = "lite_flow_chain", dicText = "chain_name", dicCode = "id")
    @Dict(dictTable = "lite_flow_chain", dicText = "chain_name", dicCode = "id")
    @ApiModelProperty(value = "流程")
    private String chainId;

    @ExcelCollection(name = "子任务表")
    @ApiModelProperty(value = "子任务表")
    private List<LiteFlowSubTask> liteFlowSubTaskList;

}
