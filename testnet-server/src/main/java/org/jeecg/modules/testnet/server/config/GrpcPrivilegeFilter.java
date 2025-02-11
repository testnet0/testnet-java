package org.jeecg.modules.testnet.server.config;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class GrpcPrivilegeFilter implements ServerInterceptor {

    @Value("${testnet.grpc.token}")
    private String grpcToken;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        // 获取客户端传递的 Token
        Metadata.Key<String> tokenKey = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
        String token = headers.get(tokenKey);
        // 检查 Token
        if (!grpcToken.equals(token)) {
            // Token 无效，返回 403 状态码
            headers.put(Metadata.Key.of("code", Metadata.ASCII_STRING_MARSHALLER), "403");
            call.close(Status.PERMISSION_DENIED.withDescription("认证失败，Token错误!"), headers);
            // 返回一个空的 Listener，表示拦截请求
            return new ServerCall.Listener<ReqT>() {};
        }

        // Token 有效，继续处理请求
        headers.put(Metadata.Key.of("code", Metadata.ASCII_STRING_MARSHALLER), "200");
        return next.startCall(call, headers);
    }
}