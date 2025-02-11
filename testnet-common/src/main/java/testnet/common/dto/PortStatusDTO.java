package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PortStatusDTO extends ResultBase {
    private String portId;
    private String isOpen;
}
