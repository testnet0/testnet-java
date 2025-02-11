import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.dto.IpOrSubDomainToPortDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * 脚本名称：Naabu 防火墙探测
 * 适用资产：子域名、IP
 * 配置：
 * command: 'naabu -no-stdin -c 200 -json -host %s -o %s -verify'
 * 结果处理类名: assetUpdateProcessor
 * 原理：随机探测3个TCP端口，大于2个端口开放则说明可能存在防火墙
 */
public class PortFirewall implements CommonScriptBody {

    private static final int MIN_PORT = 20000;
    private static final int MAX_PORT = 60000;
    private static final int PORT_COUNT = 3;
    private ILiteFlowMessageSendService messageSendService;

    public Void body(ScriptExecuteWrap wrap) {
        // 获取的是chain初始化的参数
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            messageSendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String resultPath = taskExecuteMessage.getResultPath() + "naabu_" + UUID.randomUUID() + ".json";
            messageSendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            String asset = "";
            switch (taskExecuteMessage.getAssetType()) {
                case "ip":
                    asset = instanceParams.getString("ip");
                    break;
                case "sub_domain":
                    asset = instanceParams.getString("subDomain");
                    break;
            }
            command = String.format(command, getRandomPorts(), asset, resultPath);
            messageSendService.INFO("开始执行Nabbu防火墙探测,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                BufferedReader reader = new BufferedReader(new FileReader(Paths.get(resultPath).toFile()));
                String line;
                List<IpOrSubDomainToPortDTO.Port> portList = new ArrayList<>();
                int resultCount = 0;
                while ((line = reader.readLine()) != null) {
                    // 处理每一行内容
                    JSONObject jsonObject = JSONObject.parseObject(line);
                    messageSendService.INFO("Nabbu端口扫描执行结果:{}", jsonObject);
                    resultCount = resultCount + 1;
                }
                if (resultCount >= 2) {
                    messageSendService.INFO("资产: {} 端口扫描完成，端口开放数量: {}, 可能存在防火墙", asset, resultCount);
                    instanceParams.put("assetLabel", "防火墙");
                    AssetUpdateDTO assetUpdateDTO = new AssetUpdateDTO();
                    assetUpdateDTO.setData(instanceParams.toString());
                    messageSendService.sendResult(assetUpdateDTO);
                } else {
                    messageSendService.INFO("资产: {} 端口扫描完成，端口开放数量: {}, 不存在防火墙", asset, resultCount);
                }
            } else if (result.getExitCode() == 1) {
                messageSendService.INFO("资产: {} 端口扫描完成，没有端口开放, 不存在防火墙", asset);
            } else {
                messageSendService.ERROR("Nabbu端口扫描执行失败,错误信息是:{}", result.getExitCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String getRandomPorts() {

        List<Integer> ports = new ArrayList<>();
        for (int i = MIN_PORT; i <= MAX_PORT; i++) {
            ports.add(i);
        }
        Collections.shuffle(ports);
        List<Integer> selectedPorts = ports.subList(0, PORT_COUNT);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < selectedPorts.size(); i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append(selectedPorts.get(i));
        }
        messageSendService.INFO("随机选择的端口是:{}", result);
        return result.toString();
    }
}