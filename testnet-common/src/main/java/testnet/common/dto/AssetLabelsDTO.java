/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-05-27
 **/
package testnet.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssetLabelsDTO extends ResultBase {

    private List<AssetLabelsDTO.Label> labels;


    @Setter
    @Getter
    public static class Label {
        private String labelName;
        private String labelId;
    }

}
