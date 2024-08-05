/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-05-06
 **/
package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 域名资产-ICP备案查询结果
 * @Author: TestNet
 * @Date: 2024/07/06
 */

@Getter
@Setter
public class DomainToCompanyDTO extends ResultBase {
    // 查询到的ICP备案号
    private String icpNumber;
    // 查询到的公司名称
    private String companyName;
}
