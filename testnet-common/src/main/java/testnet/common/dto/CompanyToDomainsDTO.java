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
public class CompanyToDomainsDTO extends ResultBase {
    private List<Domain> domainList;

    @Setter
    @Getter
    public static class Domain {
        private String domain;
        private String icp;
    }
}
