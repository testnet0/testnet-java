/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.AssetApi;

@Getter
@Setter
public class AssetApiDTO extends AssetApi {

    /**
     * 绝对路径
     */
    private String absolutePath;

    /**
     * 相对路径
     */
    private java.lang.String relativePath;


    private String pid;
}
