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
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 节点配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("client_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "client_config对象", description = "节点配置")
public class ClientConfig implements Serializable {
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
     * 节点
     */
    @Excel(name = "节点", width = 15, dictTable = "client", dicText = "client_name", dicCode = "id")
    @Dict(dictTable = "client", dicText = "client_name", dicCode = "id")
    @ApiModelProperty(value = "节点")
    private String clientId;
    /**
     * 配置
     */
    @Excel(name = "配置", width = 15)
    @ApiModelProperty(value = "配置")
    private String config;
    /**
     * 流程
     */
    @Excel(name = "流程", width = 15, dictTable = "lite_flow_chain", dicText = "chain_name", dicCode = "id")
    @Dict(dictTable = "lite_flow_chain", dicText = "chain_name", dicCode = "id")
    @ApiModelProperty(value = "流程")
    private String chainId;

    /**
     * 配置文件路径
     */
    @Excel(name = "配置文件路径", width = 15)
    @ApiModelProperty(value = "配置文件路径")
    private java.lang.String configPath;

    /**
     * 并发数量
     */
    @Excel(name = "并发数量", width = 15)
    @ApiModelProperty(value = "并发数量")
    private java.lang.Integer maxThreads;

}
