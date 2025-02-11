/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-03
 **/
package org.jeecg.modules.testnet.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetSearchDTO {

    private String engine;
    private String keyword;
    private Integer pageNo;
    private Integer pageSize;

    // 大规模数据查询使用
    private Integer next;

    // Fofa语法
    // 是否全量搜索 默认搜索一年内的数据，指定为true即可搜索全部数据
    private Boolean full;

    // 	Hunter语法
    // 开始时间，格式为2021-01-01
    private String startTime;
    // 结束时间，格式为2021-01-01
    private String endTime;
    //  资产类型，1代表”web资产“，2代表”非web资产“，3代表”全部“
    private String isWeb;
    // 状态码列表，以逗号分隔，如”200,401“
    private String statusCode;
    // 数据过滤参数，true为开启，false为关闭
    private String portFilter;

    // Quake语法
    // 是否忽略缓存
    private Boolean ignoreCache;
    //是否使用最新数据
    private Boolean latest;
    // 对应web页面里的 过滤无效请求 排除蜜罐 排除CDN （最新值请从WEB的url里获取）
    private String shortcuts;

    // 零零信安语法
    // 	更新时间排序：升序（ASC）、降序（DESC）
    private String timestampSort;
    // 录入时间排序：升序（ASC）、降序（DESC）
    private String exploreTimestampSort;
    // 是否自动扣费
    private Boolean zbPay;
}
