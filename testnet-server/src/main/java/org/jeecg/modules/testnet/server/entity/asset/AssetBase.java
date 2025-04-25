/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class AssetBase {
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
     * 所属项目
     */
    @Excel(name = "所属项目", width = 15, dictTable = "project", dicText = "project_name", dicCode = "id")
    @Dict(dictTable = "project", dicText = "project_name", dicCode = "id")
    @Schema(description = "所属项目")
    private java.lang.String projectId;

    /**
     * 资产标签
     */
    @Excel(name = "资产标签", width = 15, dictTable = "asset_label", dicText = "label_name", dicCode = "id")
    @Dict(dictTable = "asset_label", dicText = "label_name", dicCode = "id")
    @Schema(description = "资产标签")
    private java.lang.String assetLabel;

    /**
     * 来源
     */
    @Excel(name = "来源", width = 15)
    @Schema(description = "来源")
    private java.lang.String source;

    /**
     * 资产归属部门
     */
    @Excel(name="资产归属部门",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Schema(description = "资产归属部门")
    private java.lang.String assetDepartment;

    /**
     * 资产归属人
     */
    @Excel(name = "资产归属人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Schema(description = "资产归属人")
    private java.lang.String assetManager;
}
