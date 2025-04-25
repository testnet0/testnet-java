/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.dto;


import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetSubDomain;
import org.jeecg.modules.testnet.server.vo.asset.AssetIpVO;

import java.util.List;


@Getter
@Setter
public class AssetSubDomainIpsDTO extends AssetSubDomain {
    private String ips;
    private List<AssetIpVO> ipList;

}
