/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.testnet.server.entity.asset.AssetVul;
import org.jeecgframework.poi.excel.annotation.Excel;

@Getter
@Setter
public class AssetVulVO extends AssetVul {


    @Dict(dictTable = "asset_ip", dicText = "ip", dicCode = "id")
    @Excel(name = "IP", width = 15, dictTable = "asset_ip", dicText = "ip", dicCode = "id")
    private String ipId;

    @Dict(dictTable = "asset_sub_domain", dicText = "sub_domain", dicCode = "id")
    @Excel(name = "子域名", width = 15, dictTable = "asset_sub_domain", dicText = "sub_domain", dicCode = "id")
    private String subDomainId;

    @Dict(dictTable = "asset_web", dicText = "web_url", dicCode = "id")
    @Excel(name = "Web", width = 15, dictTable = "asset_web", dicText = "web_url", dicCode = "id")
    private String webId;

    @Dict(dictTable = "asset_port", dicText = "port", dicCode = "id")
    @Excel(name = "端口", width = 15, dictTable = "asset_port", dicText = "port", dicCode = "id")
    private String portId;
}
