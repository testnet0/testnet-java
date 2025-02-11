package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: API
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_api")
@ApiModel(value = "asset_api对象", description = "API")
public class AssetApi extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 所属api树
     */
    @Excel(name = "API", width = 15, dictTable = "asset_api_tree", dicText = "absolute_path", dicCode = "id")
    @Dict(dictTable = "asset_api_tree", dicText = "absolute_path", dicCode = "id")
    @ApiModelProperty(value = "所属api树")
    private java.lang.String assetWebTreeId;

    /**
     * 请求方法
     */
    @Excel(name = "请求方法", width = 15, dicCode = "http_method")
    @Dict(dicCode = "http_method")
    @ApiModelProperty(value = "请求方法")
    private java.lang.String httpMethod;

    /**
     * 请求头
     */
    @Excel(name = "请求头", width = 15)
    @ApiModelProperty(value = "请求头")
    private java.lang.String requestHeader;
    /**
     * 请求体
     */
    @Excel(name = "请求体", width = 15)
    @ApiModelProperty(value = "请求体")
    private java.lang.String requestBody;
    /**
     * 响应头
     */
    // @Excel(name = "响应头", width = 15)
    @ApiModelProperty(value = "响应头")
    private java.lang.String responseHeader;
    /**
     * 响应体
     */
    // @Excel(name = "响应体", width = 15)
    @ApiModelProperty(value = "响应体")
    private java.lang.String responseBody;

    /**
     * 响应包大小
     */
    @Excel(name = "响应包大小", width = 15)
    @ApiModelProperty(value = "响应包大小")
    private java.lang.Integer contentLength;


    /**
     * hash值
     */
    @Excel(name = "hash值", width = 15)
    @ApiModelProperty(value = "hash值")
    private java.lang.String hash;

    /**
     * 标题
     */
    @Excel(name = "标题", width = 15)
    @ApiModelProperty(value = "标题")
    private java.lang.String title;

    /**
     * 状态码
     */
    @Excel(name = "状态码", width = 15)
    @ApiModelProperty(value = "状态码")
    private java.lang.Integer statusCode;

    /**
     * url_path计算MD5
     */
    @Excel(name = "MD5", width = 15)
    @ApiModelProperty(value = "MD5")
    private java.lang.String pathMd5;
}
