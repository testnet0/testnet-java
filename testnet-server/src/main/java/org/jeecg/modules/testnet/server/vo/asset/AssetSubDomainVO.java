package org.jeecg.modules.testnet.server.vo.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Getter
@Setter
public class AssetSubDomainVO extends AssetSubDomain {
    private List<AssetIpVO> ipList;
    private List<AssetWeb> assetWebVOList;
    /**
     * 资产标签
     */
    @Excel(name = "资产标签", width = 15, dictTable = "asset_label", dicText = "label_name", dicCode = "id")
    @Dict(dictTable = "asset_label", dicText = "label_name", dicCode = "id")
    @Schema(description = "资产标签")
    private java.lang.String domainLabel;

    private long openPort;
}
