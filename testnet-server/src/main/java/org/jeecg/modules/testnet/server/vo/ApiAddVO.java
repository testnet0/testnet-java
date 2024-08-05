/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-24
 **/
package org.jeecg.modules.testnet.server.vo;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApiAddVO {
    private String token;
    private String assetType;
    private JSONArray assetList;
}
