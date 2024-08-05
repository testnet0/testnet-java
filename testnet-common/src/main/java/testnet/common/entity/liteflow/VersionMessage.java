/**
 * @program: testnet-client
 * @description:
 * @author: TestNet
 * @create: 2023-10-30
 **/
package testnet.common.entity.liteflow;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VersionMessage extends LiteFlowBase {
    private String clientVersion;
    private String memorySize;
    private String cpuCount;
}
