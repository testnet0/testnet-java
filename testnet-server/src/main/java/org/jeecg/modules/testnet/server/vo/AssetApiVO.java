/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetApi;

@Getter
@Setter
public class AssetApiVO extends AssetApi {

    private String absolutePath;
}
