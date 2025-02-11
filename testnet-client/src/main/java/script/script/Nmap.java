import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.IpOrSubDomainToPortDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 脚本名称：Nmap 端口扫描
 * 适用资产：子域名、IP
 * 配置：
 * command: 'nmap -T4 -sV -Pn %s -oX %s'
 * 结果处理类名: ipOrSubDomainToPortProcessor
 */
public class Nmap implements CommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        // 获取的是chain初始化的参数
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService messageSendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String resultPath = taskExecuteMessage.getResultPath() + "nmap_" + UUID.randomUUID() + ".xml";
            messageSendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            switch (taskExecuteMessage.getAssetType()) {
                case "ip":
                    command = String.format(command, instanceParams.getString("ip"), resultPath);
                    break;
                case "sub_domain":
                    command = String.format(command, instanceParams.getString("subDomain"), resultPath);
                    break;
                case "port":
                    command = String.format(command, instanceParams.getString("ip_dictText") + " -p " + instanceParams.getString("port"), resultPath);
                    break;
            }
            messageSendService.INFO("开始执行Nmap端口扫描,命令是:{}", command);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(command);
            if (result.getExitCode() == 0) {
                messageSendService.INFO("Nmap端口扫描执行完成,结果是:{}", result.getOutput());
                IpOrSubDomainToPortDTO dto = new IpOrSubDomainToPortDTO();
                List<IpOrSubDomainToPortDTO.Port> portList = new ArrayList<>();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                File file = Paths.get(resultPath).toFile();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("port");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node portNode = nodeList.item(i);
                    if (portNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element portElement = (Element) portNode;
                        // 检查端口是否为 closed 状态，若是则跳过
                        String state = portElement.getElementsByTagName("state").item(0).getAttributes().getNamedItem("state").getNodeValue();
                        if ("closed".equals(state) || "filtered".equals(state)) {
                            continue;
                        }
                        IpOrSubDomainToPortDTO.Port port = new IpOrSubDomainToPortDTO.Port();
                        // 设置端口号
                        port.setPort(Integer.parseInt(portElement.getAttribute("portid")));
                        // 设置协议
                        port.setProtocol(portElement.getAttribute("protocol"));
                        // 获取服务信息
                        NodeList serviceList = portElement.getElementsByTagName("service");
                        if (serviceList.getLength() > 0) {
                            Element serviceElement = (Element) serviceList.item(0);
                            String product = serviceElement.getAttribute("product");
                            String version = serviceElement.getAttribute("version");
                            String service = serviceElement.getAttribute("name");
                            // 设置服务名称和版本号
                            port.setService(service);
                            port.setVersion(version);
                            port.setProduct(product);
                            if (service.equals("http") || service.equals("https")) {
                                port.setIsWeb("Y");
                            } else {
                                port.setIsWeb("N");
                            }
                        }
                        // 获取所属主机信息
                        if (doc.getElementsByTagName("address") != null && doc.getElementsByTagName("address").getLength() > 0) {
                            String ip = doc.getElementsByTagName("address").item(0).getAttributes().getNamedItem("addr").getNodeValue();
                            // 设置IP地址和主机名
                            port.setIp(ip);
                        }
                        if (doc.getElementsByTagName("hostname") != null && doc.getElementsByTagName("hostname").getLength() > 0) {
                            String hostName = doc.getElementsByTagName("hostname").item(0).getAttributes().getNamedItem("name").getNodeValue();
                            port.setHost(hostName);
                        }
                        portList.add(port);
                    }
                }
                dto.setPortList(portList);
                messageSendService.sendResult(dto);
            } else {
                messageSendService.ERROR("Nmap端口扫描执行失败,错误信息是:{}", result.getExitCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}