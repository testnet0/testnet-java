/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-04-16
 **/
package testnet.common.enums;

import lombok.Getter;

@Getter
public enum AssetInstanceTypeEnums {
    ASSET_TO_INSTANCE("asset_to_instance", "资产关联实例"),
    INSTANCE_TO_ASSET("instance_to_asset", "实例关联资产");

    private final String code;
    private final String description;

    AssetInstanceTypeEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
