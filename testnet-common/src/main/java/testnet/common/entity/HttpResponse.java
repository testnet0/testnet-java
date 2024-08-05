/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-11-13
 **/
package testnet.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class HttpResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String body;
}
