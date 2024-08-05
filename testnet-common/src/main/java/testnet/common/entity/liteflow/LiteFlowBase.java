/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-04-12
 **/
package testnet.common.entity.liteflow;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LiteFlowBase {

    private String clientName;
    private String clientVersion;
    private String taskId;
    private String timestamp;
    private String retryCount;

    public LiteFlowBase() {
        this.setTimestamp(Long.toString(System.currentTimeMillis()));
        this.retryCount = "0";
    }
}
