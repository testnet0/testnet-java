/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-10-31
 **/
package testnet.common.entity.liteflow;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClientStatus extends LiteFlowBase {
    private String status;
}