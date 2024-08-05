/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-05-08
 **/
package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IpOrSubDomainToPortDTO extends ResultBase {
    private List<Port> portList;

    @Setter
    @Getter
    public static class Port {
        private String host;
        private String ip;
        private Integer port;
        private String protocol;
        private String isTls;
        private String isWeb;
        private String service;
        private String version;
        private String product;
    }

}
