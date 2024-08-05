/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2023-11-14
 **/
package testnet.common.enums;

import lombok.Getter;

@Getter
public enum LiteFlowStatusEnums {
    CREATED("任务创建"),
    PENDING("队列中"),
    RUNNING("运行中"),
    CANCELED("已取消"),
    SUCCEED("执行成功"),
    FAILED("执行失败");

    private final String statusDescription;

    LiteFlowStatusEnums(String statusDescription) {
        this.statusDescription = statusDescription;
    }

}
