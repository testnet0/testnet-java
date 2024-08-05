/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-04
 **/
package org.jeecg.modules.testnet.server.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;

import java.util.List;

@Getter
@Setter
public class AssetIpSubDomainsDTO extends AssetIp {
    private List<String> domains;
}
