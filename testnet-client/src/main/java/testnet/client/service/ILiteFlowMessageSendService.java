/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package testnet.client.service;

import testnet.common.dto.ResultBase;

public interface ILiteFlowMessageSendService {

    void setTaskId(String taskId);

    void INFO(String message, Object... args);

    void WARN(String message, Object... args);

    void ERROR(String message, Object... args);

    void sendVersion();

    void RUNNING();

    void FAILED();

    void SUCCESS();


    <T extends ResultBase> void sendResult(T result);

}
