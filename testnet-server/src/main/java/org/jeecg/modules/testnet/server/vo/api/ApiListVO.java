/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-24
 **/
package org.jeecg.modules.testnet.server.vo.api;


import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiListVO {
    private String token;
    private String assetType;
    private Integer pageSize;
    private Integer pageNo;
    private JSONObject params;
    private JSONObject queryParam;
}
