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
 * @Description: 漏洞
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_vul")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="漏洞")
public class AssetVul extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 资产类型
     */
    @Excel(name = "资产类型", width = 15, dicCode = "asset_type")
    @Dict(dicCode = "asset_type")
    @Schema(description = "资产类型")
    private String assetType;
    /**
     * 资产ID
     */
    @Excel(name = "资产", width = 15)
    @Schema(description = "资产")
    private String assetId;
    /**
     * 漏洞名称
     */
    @Excel(name = "漏洞名称", width = 15)
    @Schema(description = "漏洞名称")
    private String vulName;
    /**
     * 请求包
     */
    @Excel(name = "请求包", width = 15)
    @Schema(description = "请求包")
    private String requestBody;
    /**
     * 响应包
     */
    @Excel(name = "响应包", width = 15)
    @Schema(description = "响应包")
    private String responseBody;
    /**
     * 漏洞类型
     */
    @Excel(name = "漏洞类型", width = 15)
    @Schema(description = "漏洞类型")
    private String vulType;
    /**
     * 漏洞状态
     */
    @Excel(name = "漏洞状态", width = 15, dicCode = "vul_status")
    @Dict(dicCode = "vul_status")
    @Schema(description = "漏洞状态")
    private String vulStatus;
    /**
     * 漏洞级别
     */
    @Excel(name = "漏洞级别", width = 15, dicCode = "severity")
    @Dict(dicCode = "severity")
    @Schema(description = "漏洞级别")
    private String severity;
    /**
     * 漏洞描述
     */
    @Excel(name = "漏洞描述", width = 15)
    @Schema(description = "漏洞描述")
    private String vulDesc;

    /**
     * 漏洞链接
     */
    @Excel(name = "漏洞链接", width = 15)
    @Schema(description = "漏洞链接")
    private String vulUrl;
    /**
     * 触发请求包
     */
    @Excel(name = "触发请求包", width = 15)
    @Schema(description = "触发请求包")
    private String payload;


    /**
     * 漏洞负责人
     */
    @Excel(name = "漏洞负责人", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Schema(description = "漏洞负责人")
    private java.lang.String owner;

}
