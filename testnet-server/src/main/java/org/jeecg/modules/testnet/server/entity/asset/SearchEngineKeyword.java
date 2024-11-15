package org.jeecg.modules.testnet.server.entity.asset;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 搜索引擎语法
 * @Author: jeecg-boot
 * @Date:   2024-09-12
 * @Version: V1.0
 */
@Data
@TableName("search_engine_keyword")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="search_engine_keyword对象", description="搜索引擎语法")
public class SearchEngineKeyword implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**引擎*/
    @Excel(name = "引擎名称", width = 15, dicCode = "engine_name")
    @Dict(dicCode = "engine_name")
    @ApiModelProperty(value = "引擎")
    private String engine;
	/**语法*/
	@Excel(name = "语法", width = 15)
    @ApiModelProperty(value = "语法")
    private String keyword;
	/**类型*/
	@Excel(name = "类型", width = 15, dicCode = "search_keyword_type")
    @Dict(dicCode = "search_keyword_type")
    @ApiModelProperty(value = "类型")
    private String type;

    /**例句*/
    @Excel(name = "例句", width = 15)
    @ApiModelProperty(value = "例句")
    private java.lang.String example;
    /**说明*/
    @Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
    private java.lang.String remark;
}
