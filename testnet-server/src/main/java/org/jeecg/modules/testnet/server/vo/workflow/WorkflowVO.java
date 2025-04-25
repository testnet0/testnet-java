package org.jeecg.modules.testnet.server.vo.workflow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowVO {
    private String flowId;
    private String edges;
    private String nodes;
}
