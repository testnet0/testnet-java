/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.entity.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToDoCard {

    // 标题
    private String title;
    // 图标
    private String icon;
    // 颜色
    private String color;
    // 总数
    private long totalCount;
    // 今日新增数量
    private long todayIncreaseCount;
    // 跳转链接
    private String href;

}
