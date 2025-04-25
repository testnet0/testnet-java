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
 * @Description: 搜索引擎语法
 * @Author: jeecg-boot
 * @Date: 2024-09-12
 * @Version: V1.0
 */
@Data
@TableName("search_engine_keyword")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="搜索引擎语法")
public class SearchEngineKeyword implements Serializable {
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
     * 引擎
     */
    @Excel(name = "引擎名称", width = 15, dicCode = "engine_name")
    @Dict(dicCode = "engine_name")
    @Schema(description = "引擎")
    private String engine;
    /**
     * 语法
     */
    @Excel(name = "语法", width = 15)
    @Schema(description = "语法")
    private String keyword;
    /**
     * 类型
     */
    @Excel(name = "类型", width = 15, dicCode = "search_keyword_type")
    @Dict(dicCode = "search_keyword_type")
    @Schema(description = "类型")
    private String type;

    /**
     * 例句
     */
    @Excel(name = "例句", width = 15)
    @Schema(description = "例句")
    private java.lang.String example;
    /**
     * 说明
     */
    @Excel(name = "说明", width = 15)
    @Schema(description = "说明")
    private java.lang.String remark;
}
