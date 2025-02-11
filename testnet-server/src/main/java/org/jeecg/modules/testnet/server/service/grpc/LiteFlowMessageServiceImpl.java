package org.jeecg.modules.testnet.server.service.grpc;

import cn.hutool.crypto.digest.DigestUtil;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;
import org.jeecg.modules.testnet.server.entity.liteflow.Script;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.IScriptService;
import testnet.grpc.LiteFlowMessageProto.*;
import testnet.grpc.LiteFlowMessageServiceGrpc;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class LiteFlowMessageServiceImpl extends LiteFlowMessageServiceGrpc.LiteFlowMessageServiceImplBase {

    @Resource
    private IScriptService scriptService;

    @Resource
    private IChainService chainService;


    @Override
    public void getFlowHashes(FlowRequest request, StreamObserver<FlowHashesResponse> responseObserver) {
        List<ChainMessage> chains = fetchAllChains();
        List<ScriptMessage> scripts = fetchAllScripts();
        Map<String, String> chainHashes = chains.stream().collect(Collectors.toMap(ChainMessage::getId, this::calculateChainHash));
        Map<String, String> scriptHashes = scripts.stream().collect(Collectors.toMap(ScriptMessage::getId, this::calculateScriptHash));
        FlowHashesResponse response = FlowHashesResponse.newBuilder().putAllChainHashes(chainHashes).putAllScriptHashes(scriptHashes).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getChainById(FlowByIdRequest request, StreamObserver<ChainMessage> responseObserver) {
        ChainMessage chain = fetchChainById(request.getId());
        responseObserver.onNext(chain);
        responseObserver.onCompleted();
    }

    @Override
    public void getScriptById(FlowByIdRequest request, StreamObserver<ScriptMessage> responseObserver) {
        ScriptMessage script = fetchScriptById(request.getId());
        responseObserver.onNext(script);
        responseObserver.onCompleted();
    }

    private String calculateChainHash(ChainMessage chain) {
        String data = chain.getEnabled() + chain.getChainId() + chain.getBody();
        return DigestUtil.md5Hex(data);
    }

    private String calculateScriptHash(ScriptMessage script) {
        String data = script.getEnabled() + script.getLanguage() + script.getScript() + script.getType();
        return DigestUtil.md5Hex(data);
    }

    private List<ChainMessage> fetchAllChains() {
        List<Chain> chains = chainService.list();

        return chains.stream().map(this::convertToChainMessage).collect(Collectors.toList());
    }

    private List<ScriptMessage> fetchAllScripts() {
        List<Script> scripts = scriptService.list();
        return scripts.stream().map(this::convertToScriptMessage).collect(Collectors.toList());
    }

    private ChainMessage fetchChainById(String chainId) {
        return convertToChainMessage(chainService.getById(chainId));
    }

    private ScriptMessage fetchScriptById(String scriptId) {
        return convertToScriptMessage(scriptService.getById(scriptId));
    }

    /**
     * 将 Script 实体转换为 ScriptMessage 对象
     */
    private ScriptMessage convertToScriptMessage(Script script) {
        return ScriptMessage.newBuilder().setId(script.getId()).setNodeId(script.getScriptId()).setType(script.getScriptType()).setName(script.getScriptName()).setApplicationName(script.getApplicationName()).setLanguage(script.getScriptLanguage()).setScript(script.getScriptData()).setEnabled(script.getEnable()).build();
    }

    private ChainMessage convertToChainMessage(Chain chain) {
        return ChainMessage.newBuilder().setId(chain.getId()).setChainId(chain.getChainName()).setApplicationName(chain.getApplicationName()).setBody(chain.getElData()).setEnabled(chain.getEnable()).build();
    }
}