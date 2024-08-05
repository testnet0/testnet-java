/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-05-09
 **/
package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpOrSubDomainOrPortToWebDTO extends ResultBase {
    private String port;
    private String webUrl;
    private String webTitle;
    private String webHeader;
    private String httpSchema;
    private String contentType;
    private String method;
    private String ip;
    private String domain;
    private String path;
    private String delayTime;
    private Integer statusCode;
    private Integer contentLength;
    private String headerMd5;
    private String bodyMd5;
    private String jarm;
    private String favicon;
    private String screenshot;
    private String responseBody;
    private String tech;
    private String source;
}
