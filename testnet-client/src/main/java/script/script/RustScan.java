import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang.StringUtils;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.IpOrSubDomainToPortDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 脚本名称：RustScan 端口扫描
 * 适用资产：子域名、IP
 * 配置：
 * command: '/testnet-client/tools/rustscan -a %s -p 80,443,22,21,23,25,53,110,143,161,389,445,587,3306,3389,8080,8443,53,137,139,445,993,995,1723,111,5900,5901,3128,8081,8082,9090,10000,10001 -g'
 * 结果处理类名: ipOrSubDomainToPortProcessor
 */
public class RustScan implements CommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        // 获取的是chain初始化的参数
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService messageSendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String resultPath = taskExecuteMessage.getResultPath() + "rustscan_" + UUID.randomUUID() + ".json";
            messageSendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            String ip = "";
            String domain = "";
            switch (taskExecuteMessage.getAssetType()) {
                case "ip":
                    ip = instanceParams.getString("ip");
                    break;
                case "sub_domain":
                    domain = instanceParams.getString("subDomain");
                    break;
            }
            if (StringUtils.isNotBlank(domain)) {
                command = String.format(command, domain, resultPath);
            } else {
                command = String.format(command, ip, resultPath);
            }
            messageSendService.INFO("开始执行RustScan端口扫描,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                IpOrSubDomainToPortDTO dto = new IpOrSubDomainToPortDTO();
                List<IpOrSubDomainToPortDTO.Port> portList = new ArrayList<>();
                messageSendService.INFO("RustScan端口扫描执行结果:{}", result.getOutput());
                Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+) -> \\[(.*?)\\]");
                Matcher matcher = pattern.matcher(result.getOutput());
                while (matcher.find()) {
                    String resultIp = matcher.group(1); // IP地址
                    String ports = matcher.group(2); // 端口号列表
                    for (int i = 0; i < ports.split(",").length; i++) {
                        IpOrSubDomainToPortDTO.Port port = new IpOrSubDomainToPortDTO.Port();
                        port.setIp(resultIp);
                        port.setPort(Integer.parseInt(ports.split(",")[i]));
                        port.setHost(domain);
                        portList.add(port);
                    }
                }
                dto.setPortList(portList);
                messageSendService.sendResult(dto);
            } else if (result.getExitCode() == 1) {
                messageSendService.INFO("资产: {} 端口扫描完成，没有端口开放");
            } else {
                messageSendService.ERROR("RustScan端口扫描执行失败,错误信息是:{}", result.getExitCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}