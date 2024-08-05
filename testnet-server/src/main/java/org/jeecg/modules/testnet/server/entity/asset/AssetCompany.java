package org.jeecg.modules.testnet.server.entity.asset;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 公司
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Data
@TableName("asset_company")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AssetCompany extends AssetBase implements Serializable {

    /**
     * 公司名称
     */
    @Excel(name = "公司名", width = 15)
    private java.lang.String companyName;

}
