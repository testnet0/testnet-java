/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.Project;

@Getter
@Setter
public class ProjectVO extends Project {

    private long assetDomainCount;

    private long assetIPCount;

    private long assetCompanyCount;

    private long assetSubDomainCount;

    private long assetPortCount;

    private long assetWebCount;

    private long assetApiCount;

    private long assetVulCount;


}
