package testnet.common.entity.liteflow;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskExecuteMessage extends LiteFlowBase {
    private String assetType;
    private String chainName;
    private String config;
    private String chainId;
    private String taskParams;
    private String resultPath;
}
