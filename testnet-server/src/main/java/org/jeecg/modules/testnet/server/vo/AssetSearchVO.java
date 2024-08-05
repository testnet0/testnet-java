/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-03
 **/
package org.jeecg.modules.testnet.server.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetSearchVO {
    private String title;      // 网站标题
    private String url;        // 访问链接
    private String domain;
    private List<String> domains;  // 网站域名
    private String cert;       // 证书内容
    private String ip;         // IP或IP段
    private Integer port;       // 端口
    private String protocol;   // 协议
    private String isWeb;      // 是否为Web
    private int statusCode;    // Web状态码

    private String server;     // HTTP headers里面的Server字段
    private String baseProtocol;// 传输层协议
    private String component;  // 组件
    private String os;         // 系统
    private String asn;        // 自治域号码
    private String iconHash;   // 图标hash
    private String iconUrl;    // 图标URL


    private String app;        // 应用指纹
    private String region;     // 地区
    private String country;    // 国家
    private String province;   // 省
    private String city;       // 城市
    private String isp;        // 运营商

    private String icpNumber;        // ICP备案号
    private String company;    // 公司

    private String banner;    // 返回头
    private String header;
    private String body;       // 正文，或者说响应体

    private String responseHash; // 响应体hash
}