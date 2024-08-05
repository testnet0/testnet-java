/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Data
@TableName("asset_api_tree")
public class AssetApiTree extends AssetBase implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 所属WEB
     */
    @Excel(name = "所属WEB", width = 15, dictTable = "asset_web", dicText = "web_url", dicCode = "id")
    @Dict(dictTable = "asset_web", dicText = "web_url", dicCode = "id")
    @ApiModelProperty(value = "所属WEB")
    private java.lang.String assetWebId;

    /**
     * 相对路径
     */
    @Excel(name = "相对路径", width = 15)
    @ApiModelProperty(value = "相对路径")
    private java.lang.String relativePath;

    /**
     * 绝对路径
     */
    @Excel(name = "绝对路径", width = 15)
    @ApiModelProperty(value = "绝对路径")
    private java.lang.String absolutePath;

    /**
     * 父级节点
     */
    @Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private java.lang.String pid;
    /**
     * 是否有子节点
     */
    @Excel(name = "是否有子节点", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;


}
