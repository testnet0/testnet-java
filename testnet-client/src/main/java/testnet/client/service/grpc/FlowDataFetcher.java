package testnet.client.service.grpc;

import com.yomahub.liteflow.builder.LiteFlowNodeBuilder;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import com.yomahub.liteflow.flow.FlowBus;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import testnet.client.config.EnvConfig;
import testnet.grpc.LiteFlowMessageProto.*;
import testnet.grpc.LiteFlowMessageServiceGrpc;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlowDataFetcher {

    @GrpcClient("myService")
    private LiteFlowMessageServiceGrpc.LiteFlowMessageServiceBlockingStub liteFlowMessageServiceBlockingStub;

    // 存储 chain 的哈希值
    private final Map<String, String> chainHashMap = new HashMap<>();

    // 存储 script 的哈希值
    private final Map<String, String> scriptHashMap = new HashMap<>();

    @Resource
    private EnvConfig envConfig;

    /**
     * 拉取哈希值并更新数据
     */
    public void pullAndUpdateFlowData() {

        // 拉取哈希值
        FlowRequest request = FlowRequest.newBuilder()
                .build();
        FlowHashesResponse response = liteFlowMessageServiceBlockingStub.getFlowHashes(request);

        // 更新 script
        updateScripts(response.getScriptHashesMap());
        // 更新 chain
        updateChains(response.getChainHashesMap());
    }

    /**
     * 更新 chain 数据
     */
    private void updateChains(Map<String, String> newChainHashes) {
        // 新增或更新的 chain
        for (Map.Entry<String, String> entry : newChainHashes.entrySet()) {
            String id = entry.getKey();
            String newHash = entry.getValue();
            String oldHash = chainHashMap.get(id);

            if (!newHash.equals(oldHash)) {
                // 拉取具体的 chain 内容
                try {
                    ChainMessage chainMessage = liteFlowMessageServiceBlockingStub.getChainById(FlowByIdRequest.newBuilder().setId(id).build());
                    LiteFlowChainELBuilder.createChain()
                            .setChainId(chainMessage.getChainId())
                            .setRoute(chainMessage.getRoute())
                            .setNamespace(chainMessage.getNamespace())
                            .setEL(chainMessage.getBody())
                            .build();

                } catch (Exception e) {
                    log.error("拉取工作流失败，原因:{}", e.getMessage());
                }
                chainHashMap.put(id, newHash);
            }
        }

        // 删除的 chain
        Set<String> chainsToDelete = chainHashMap.keySet().stream()
                .filter(chainId -> !newChainHashes.containsKey(chainId))
                .collect(Collectors.toSet());
        for (String chainId : chainsToDelete) {
            FlowBus.removeChain(chainId);
            chainHashMap.remove(chainId);
        }
    }

    /**
     * 更新 script 数据
     */
    private void updateScripts(Map<String, String> newScriptHashes) {
        // 新增或更新的 script
        for (Map.Entry<String, String> entry : newScriptHashes.entrySet()) {
            String scriptId = entry.getKey();
            String newHash = entry.getValue();
            String oldHash = scriptHashMap.get(scriptId);

            if (!newHash.equals(oldHash)) {
                // 拉取具体的 script 内容
                try {
                    ScriptMessage scriptMessage = liteFlowMessageServiceBlockingStub.getScriptById(FlowByIdRequest.newBuilder().setId(scriptId).build());
                    LiteFlowNodeBuilder.createScriptNode()
                            .setId(scriptMessage.getNodeId())
                            .setType(NodeTypeEnum.getEnumByCode(scriptMessage.getType()))
                            .setName(scriptMessage.getName())
                            .setScript(scriptMessage.getScript())
                            .setLanguage(scriptMessage.getLanguage())
                            .build();
                } catch (Exception e) {
                    log.error("拉取脚本失败,原因:{}", e.getMessage());
                }
                scriptHashMap.put(scriptId, newHash); // 更新哈希值
            }
        }

        // 删除的 script
        Set<String> scriptsToDelete = scriptHashMap.keySet().stream()
                .filter(scriptId -> !newScriptHashes.containsKey(scriptId))
                .collect(Collectors.toSet());
        for (String scriptId : scriptsToDelete) {
            FlowBus.unloadScriptNode(scriptId);
            scriptHashMap.remove(scriptId);
        }
    }

    /**
     * 定时拉取并更新 Chain 和 Script 数据
     */
    @Scheduled(fixedDelay = 60000) // 每60秒执行一次
    public void scheduledPullAndUpdateFlowData() {
        pullAndUpdateFlowData();
    }
}