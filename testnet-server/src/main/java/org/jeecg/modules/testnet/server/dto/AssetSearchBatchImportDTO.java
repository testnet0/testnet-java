package org.jeecg.modules.testnet.server.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetSearchBatchImportDTO extends AssetSearchImportDTO {
    private Integer batchSize;
    private Integer batchCount;
}
