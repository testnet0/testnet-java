/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-13
 **/
package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetApiToApiDTO extends ResultBase {

    private List<AssetApiDTO> assetApiDTOList;


    @Setter
    @Getter
    public static class AssetApiDTO {
        private String absolutePath;
        private String httpMethod;
        private String requestHeader;
        private String requestBody;
        private String responseHeader;
        private String responseBody;
        private Integer contentLength;
        private Integer statusCode;
        private String contentType;
        private String title;
    }
}
