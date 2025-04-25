/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;
import org.jeecg.modules.testnet.server.vo.asset.AssetSubDomainVO;

import java.util.List;

@Getter
@Setter
public class AssetPortDTO extends AssetPort {
    private List<AssetSubDomainVO> domains;
    private String ip_dictText;
    private String portRange;

}
