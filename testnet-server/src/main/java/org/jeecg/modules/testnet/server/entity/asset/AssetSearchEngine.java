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

/**
 * @Description: 空间引擎配置
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_search_engine")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="空间引擎配置")
public class AssetSearchEngine implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 引擎名称
     */
    @Excel(name = "引擎名称", width = 15, dicCode = "engine_name")
    @Dict(dicCode = "engine_name")
    @Schema(description = "引擎名称")
    private java.lang.String engineName;
    /**
     * 引擎Token
     */
    @Excel(name = "引擎Token", width = 15)
    @Schema(description = "引擎Token")
    private java.lang.String engineToken;

    /**
     * 引擎Host地址
     */
    @Excel(name = "引擎Host地址", width = 15)
    @Schema(description = "引擎Host地址")
    private java.lang.String engineHost;
}
