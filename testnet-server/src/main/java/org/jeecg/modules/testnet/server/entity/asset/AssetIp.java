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
 * @Description: ip
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_ip")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="ip")
public class AssetIp extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ip
     */
    @Excel(name = "ip", width = 15)
    @Schema(description = "ip")
    private java.lang.String ip;
    /**
     * 公网
     */
    @Excel(name = "公网", width = 15, dicCode = "is_open")
    @Dict(dicCode = "is_open")
    @Schema(description = "公网")
    private java.lang.String isPublic;
    /**
     * ipv6
     */
    @Excel(name = "ipv6", width = 15, dicCode = "is_open")
    @Dict(dicCode = "is_open")
    @Schema(description = "ipv6")
    private java.lang.String isIpv6;


    /**
     * 国家
     */
    @Excel(name = "国家", width = 15)
    @Schema(description = "国家")
    private java.lang.String country;

    /**
     * 地区
     */
    @Excel(name = "地区", width = 15)
    @Schema(description = "地区")
    private java.lang.String region;

    /**
     * 省份
     */
    @Excel(name = "省份", width = 15)
    @Schema(description = "省份")
    private java.lang.String province;
    /**
     * 城市
     */
    @Excel(name = "城市", width = 15)
    @Schema(description = "城市")
    private java.lang.String city;

    /**
     * 运营商
     */
    @Excel(name = "运营商", width = 15)
    @Schema(description = "运营商")
    private java.lang.String isp;

}
