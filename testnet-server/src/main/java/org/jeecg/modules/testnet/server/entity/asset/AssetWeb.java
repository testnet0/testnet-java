package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: WEB服务
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_web")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="WEB服务")
public class AssetWeb extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 站点标题
     */
    @Excel(name = "站点标题", width = 15)
    @Schema(description = "站点标题")
    private java.lang.String webTitle;
    /**
     * Header
     */
    // @Excel(name = "Header", width = 15)
    @Schema(description = "Header")
    private java.lang.String webHeader;

    /**
     * Icon_hash
     * mmh3 hash for '/favicon.ico' file
     */
    @Excel(name = "Icon_hash", width = 15)
    @Schema(description = "Icon_hash")
    private java.lang.String favicon;

    /**
     * Icon_url
     * s3 url for '/favicon.ico' file
     */
    @Excel(name = "Icon_url", width = 15)
    @Schema(description = "Icon_url")
    private java.lang.String iconUrl;

    /**
     * response body hash
     */
    @Excel(name = "bodyMd5", width = 15)
    @Schema(description = "body_md5")
    private java.lang.String bodyMd5;

    /**
     * response header hash
     */
    @Excel(name = "headerMd5", width = 15)
    @Schema(description = "header_md5")
    private java.lang.String headerMd5;

    /**
     * jarm fingerprint hash
     */
    @Excel(name = "hash", width = 15)
    @Schema(description = "jarm")
    private java.lang.String jarm;

    /**
     * 所属域名
     */
    @Excel(name = "所属子域名", width = 15, dictTable = "asset_sub_domain", dicText = "sub_domain", dicCode = "id")
    @Dict(dictTable = "asset_sub_domain", dicText = "sub_domain", dicCode = "id")
    @Schema(description = "所属子域名")
    private java.lang.String domain;

    /**
     * 端口
     */
    @Excel(name = "端口", width = 15, dictTable = "asset_port", dicText = "port", dicCode = "id")
    @Dict(dictTable = "asset_port", dicText = "port", dicCode = "id")
    @Schema(description = "端口")
    private java.lang.String portId;
    /**
     * 站点截图
     */
    @Excel(name = "站点截图", width = 15)
    @Schema(description = "站点截图")
    private java.lang.String screenshot;

    /**
     * 访问链接
     */
    @Excel(name = "访问链接", width = 15)
    @Schema(description = "访问链接")
    private java.lang.String webUrl;
    /**
     * 服务器
     */
    @Excel(name = "服务器", width = 15)
    @Schema(description = "服务器")
    private java.lang.String webServer;
    /**
     * content_type
     */
    @Excel(name = "content_type", width = 15)
    @Schema(description = "content_type")
    private java.lang.String contentType;

    /**
     * 延迟
     */
    @Excel(name = "延迟", width = 15)
    @Schema(description = "延迟")
    private java.lang.String delayTime;
    /**
     * 技术框架
     */
    @Excel(name = "技术框架", width = 15)
    @Schema(description = "技术框架")
    private java.lang.String tech;
    /**
     * 状态码
     */
    @Excel(name = "状态码", width = 15)
    @Schema(description = "状态码")
    private java.lang.Integer statusCode;
    /**
     * content_length
     */
    @Excel(name = "content_length", width = 15)
    @Schema(description = "content_length")
    private java.lang.Integer contentLength;
    /**
     * http协议
     */
    @Excel(name = "http协议", width = 15)
    @Schema(description = "http协议")
    private java.lang.String httpSchema;

}


