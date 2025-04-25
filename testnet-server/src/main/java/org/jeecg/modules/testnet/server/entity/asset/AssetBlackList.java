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
import java.util.Date;

/**
 * @Description: 黑名单
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_black_list")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="黑名单")
public class AssetBlackList implements Serializable {
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
     * 资产类型
     */
    @Excel(name = "资产类型", width = 15, dicCode = "asset_type")
    @Dict(dicCode = "asset_type")
    @Schema(description = "资产类型")
    private String assetType;

    /**
     * 关键字
     */
    @Excel(name = "关键字", width = 15)
    @Schema(description = "关键字")
    private String keyword;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remark;

    /**
     * 黑名单类型
     */
    @Excel(name = "黑名单类型", width = 15, dicCode = "blacklist_type")
    @Dict(dicCode = "blacklist_type")
    @Schema(description = "黑名单类型")
    private String blacklistType;
}
