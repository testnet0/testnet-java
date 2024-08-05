package testnet.common.entity.liteflow;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import testnet.common.dto.ResultBase;

@Getter
@Setter
@ToString
public class ClientToolVersion extends ResultBase {
    private String toolVersion;
}
