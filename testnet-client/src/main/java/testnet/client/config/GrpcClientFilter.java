package testnet.client.config;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.beans.factory.annotation.Value;

import static testnet.common.constan.Constants.TOKEN_HEADER;

@GrpcGlobalClientInterceptor
public class GrpcClientFilter implements ClientInterceptor {
    // 客户端header的key
    static final Metadata.Key<String> TOKEN = Metadata.Key.of(TOKEN_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    @Value("${testnet.grpc.token}")
    private String token;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // 放入客户端的header
                headers.put(TOKEN, token);
                super.start(
                        new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                        }, headers);
            }
        };
    }

}