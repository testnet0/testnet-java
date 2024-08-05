package testnet.common.enums;

import lombok.Getter;

@Getter
public enum AssetTypeEnums {
    DOMAIN("domain", "域名"),
    SUB_DOMAIN("sub_domain", "子域名"),
    IP("ip", "IP"),
    PORT("port", "端口"),
    WEB("web", "WEB服务"),
    VUL("vul", "漏洞"),
    API("api", "API"),
    COMPANY("company", "公司");

    private final String code;
    private final String description;

    AssetTypeEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AssetTypeEnums fromCode(String code) {
        for (AssetTypeEnums resource : values()) {
            if (resource.getCode().equals(code)) {
                return resource;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

}
