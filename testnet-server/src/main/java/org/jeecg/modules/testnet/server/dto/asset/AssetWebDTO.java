package org.jeecg.modules.testnet.server.dto.asset;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;


@Getter
@Setter
public class AssetWebDTO extends AssetWeb {
    private String body;
}
