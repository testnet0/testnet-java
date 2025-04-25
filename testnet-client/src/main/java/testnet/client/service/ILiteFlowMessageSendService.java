/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package testnet.client.service;

import testnet.common.dto.ResultBase;
import testnet.grpc.ClientMessageProto;

import java.util.function.Function;

public interface ILiteFlowMessageSendService {

    void setTaskId(String taskId);

    void INFO(String message, Object... args);

    void WARN(String message, Object... args);

    void ERROR(String message, Object... args);

    void RUNNING();

    void FAILED();

    void SUCCESS();

    <T> ClientMessageProto.ClientResponse sendWithRetryAndFallback(String type, T data, Function<T, ClientMessageProto.ClientResponse> sendFunction);

    <T extends ResultBase> ClientMessageProto.ClientResponse sendResult(T result);


}
