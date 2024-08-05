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
 * @Description: 主域名
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_domain")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "asset_domain对象", description = "主域名")
public class AssetDomain extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主域名
     */
    @Excel(name = "主域名", width = 15)
    @ApiModelProperty(value = "主域名")
    private java.lang.String domain;
    /**
     * ICP备案号
     */
    @Excel(name = "ICP备案号", width = 15)
    @ApiModelProperty(value = "ICP备案号")
    private java.lang.String icpNumber;
    /**
     * whois
     */
    @Excel(name = "whois", width = 15)
    @ApiModelProperty(value = "whois")
    private java.lang.String whois;

    /**
     * whois
     */
    @Excel(name = "dns服务器", width = 15)
    @ApiModelProperty(value = "dns服务器")
    private java.lang.String dnsServer;

    /**
     * 所属公司
     */
    @Excel(name = "所属公司", width = 15, dictTable = "asset_company", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "asset_company", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "所属公司")
    private java.lang.String companyId;

}
