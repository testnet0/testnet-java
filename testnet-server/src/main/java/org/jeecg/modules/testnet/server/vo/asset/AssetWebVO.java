/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.vo.asset;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.testnet.server.entity.asset.AssetWeb;

@Getter
@Setter
public class AssetWebVO extends AssetWeb {

    private String body;


    @Dict(dictTable = "asset_ip", dicText = "ip", dicCode = "id")
    private String ip;


}
