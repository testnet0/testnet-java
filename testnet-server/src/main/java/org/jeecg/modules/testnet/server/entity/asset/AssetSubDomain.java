package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 子域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_sub_domain")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="子域名")
public class AssetSubDomain extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 子域名
     */
    @Excel(name = "子域名", width = 15)
    @Schema(description = "子域名")
    public java.lang.String subDomain;

    /**
     * 解析类型
     */
    @Excel(name = "解析类型", width = 15, dicCode = "dns_type")
    @Dict(dicCode = "dns_type")
    @Schema(description = "解析类型")
    private java.lang.String type;

    /**
     * 解析值
     */
    @Excel(name = "解析值", width = 15)
    @Schema(description = "解析值")
    private java.lang.String dnsRecord;
    /**
     * name_server
     */
    @Excel(name = "name_server", width = 15)
    @Schema(description = "name_server")
    private java.lang.String nameServer;

    /**
     * 域名等级
     */
    @Excel(name = "域名等级", width = 15)
    @Schema(description = "域名等级")
    private java.lang.Integer level;
    /**
     * 主域名
     */
    @Excel(name = "主域名", width = 15, dictTable = "asset_domain", dicText = "domain", dicCode = "id")
    @Dict(dictTable = "asset_domain", dicText = "domain", dicCode = "id")
    @Schema(description = "主域名")
    private java.lang.String domainId;

    @Excel(name = "IP", width = 15)
    @TableField(exist = false)
    private String ip;

}
