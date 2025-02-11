package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 端口
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_port")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "asset_port对象", description = "端口")
public class AssetPort extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 端口号
     */
    @Excel(name = "端口号", width = 15)
    @ApiModelProperty(value = "端口号")
    private java.lang.Integer port;
    /**
     * 所属ip
     */
    @Excel(name = "所属ip", width = 15, dictTable = "asset_ip", dicText = "ip", dicCode = "id")
    @Dict(dictTable = "asset_ip", dicText = "ip", dicCode = "id")
    @ApiModelProperty(value = "所属ip")
    private java.lang.String ip;


    /**
     * 协议
     */
    @Excel(name = "协议", width = 15)
    @ApiModelProperty(value = "协议")
    private java.lang.String protocol;

    /**
     * 服务
     */
    @Excel(name = "服务", width = 15)
    @ApiModelProperty(value = "服务")
    private java.lang.String service;


    /**
     * 产品
     */
    @Excel(name = "产品", width = 15)
    @ApiModelProperty(value = "产品")
    private java.lang.String product;
    /**
     * 版本
     */
    @Excel(name = "版本", width = 15)
    @ApiModelProperty(value = "版本")
    private java.lang.String version;
    /**
     * Web资产
     */
    @Excel(name = "Web资产", width = 15)
    @ApiModelProperty(value = "Web资产")
    private java.lang.String isWeb;

    /**
     * 是否TLS
     */
    @Excel(name = "是否TLS", width = 15)
    @ApiModelProperty(value = "是否TLS")
    private java.lang.String isTls;

}
