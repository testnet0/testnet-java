/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-06-24
 **/
package testnet.common.entity.workflow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    // 节点id
    private String id;
    // 节点名称
    private String name;
    // 节点类型
    private String type;
    // 节点输入
    private String input;
    // 节点输出
    private String output;
}
