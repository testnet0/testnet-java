/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiteflowInstanceLogVO {
    private String level;
    private String clientName;
    private String message;
    private String instanceId;
    private String timestamp;
}
