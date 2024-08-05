/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-05-11
 **/
package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IpOrWebOrSubDomainToVulDTO extends ResultBase {
    private List<AssetVul> assetVulList;

    @Getter
    @Setter
    public static class AssetVul {
        private String vulName;
        private String requestBody;
        private String responseBody;
        private String severity;
        private String vulDesc;
        private String vulUrl;
        private String payload;
    }
}
