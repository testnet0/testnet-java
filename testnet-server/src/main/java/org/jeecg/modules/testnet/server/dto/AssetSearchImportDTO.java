/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-03
 **/
package org.jeecg.modules.testnet.server.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.vo.asset.AssetSearchVO;

import java.util.List;

@Getter
@Setter
public class AssetSearchImportDTO {

    private String engine;

    private AssetSearchDTO params;

    private String projectId;

    private List<AssetSearchVO> data;

}
