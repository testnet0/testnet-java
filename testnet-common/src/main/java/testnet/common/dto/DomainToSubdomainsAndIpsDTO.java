/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-05-06
 **/
package testnet.common.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DomainToSubdomainsAndIpsDTO extends ResultBase {
    // 子域名列表
    private List<SudDomain> subDomainList;

    @Setter
    @Getter
    public static class SudDomain {
        private String subDomain;
        private String ipList;
        private String type;
        private Integer level;
        private String source;
    }
}