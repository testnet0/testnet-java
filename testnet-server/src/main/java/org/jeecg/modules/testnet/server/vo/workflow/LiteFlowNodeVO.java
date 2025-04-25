package org.jeecg.modules.testnet.server.vo.workflow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiteFlowNodeVO {

    private String id;

    private String name;

    private String type;

    private String shape;

    private String inputAsset;

    private String outputAsset;
}
