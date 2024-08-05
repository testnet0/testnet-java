package org.jeecg.modules.testnet.server.vo.asset;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;

import java.util.List;

@Getter
@Setter
public class AssetPortVO extends AssetPort {
    private List<AssetSubDomainVO> domains;
}
