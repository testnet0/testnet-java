package org.jeecg.modules.testnet.server.vo.asset;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;

import java.util.List;

@Getter
@Setter
public class AssetSubDomainVO extends AssetSubDomain {
    private List<AssetIpVO> ipList;
    private List<AssetWeb> assetWebVOList;
    private long openPort;
}
