/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-24
 **/
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

/**
 * @Description: 节点工具
 * @Author: jeecg-boot
 * @Date: 2024-07-24
 * @Version: V1.0
 */
@Data
@TableName("client_tools")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "client_tools对象", description = "节点工具")
public class ClientTools implements Serializable {
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
     * 节点
     */
    @Excel(name = "节点", width = 15, dictTable = "client", dicText = "client_name", dicCode = "id")
    @Dict(dictTable = "client", dicText = "client_name", dicCode = "id")
    @ApiModelProperty(value = "节点")
    private java.lang.String clientId;
    /**
     * 工具
     */
    @Excel(name = "工具", width = 15, dictTable = "lite_flow_script", dicText = "script_ame", dicCode = "id")
    @Dict(dictTable = "lite_flow_script", dicText = "script_name", dicCode = "id")
    @ApiModelProperty(value = "工具")
    private java.lang.String scriptId;
    /**
     * 安装状态
     */
    @Excel(name = "安装状态", width = 15)
    @ApiModelProperty(value = "安装状态")
    private Boolean status;
    /**
     * 版本
     */
    @Excel(name = "版本", width = 15)
    @ApiModelProperty(value = "版本")
    private java.lang.String version;

    /**
     * 自动安装
     */
    @Excel(name = "自动安装", width = 15)
    @ApiModelProperty(value = "自动安装")
    private Boolean autoInstall;
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
