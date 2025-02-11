/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.vo.asset;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetDomain;

@Getter
@Setter
public class AssetDomainVO extends AssetDomain {

    private long subDomainNumber;

}
