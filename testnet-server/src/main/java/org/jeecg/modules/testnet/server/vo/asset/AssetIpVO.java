/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-04-18
 **/
package org.jeecg.modules.testnet.server.vo.asset;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;

import java.util.List;

@Getter
@Setter
public class AssetIpVO extends AssetIp {
    private List<AssetSubDomainVO> domainVOList;
    private long portCount;
    private String subDomainIds;
}
