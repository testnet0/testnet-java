import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang.StringUtils;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.client.util.TencentCOSUtils;
import testnet.common.dto.IpOrSubDomainOrPortToWebDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.CommandUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 脚本名称：httpx web探测
 * 适用资产：端口、子域名、IP
 * 配置：
 * command: 'httpx -disable-update-check -no-stdin -hash md5 -favicon -jarm -cdn -json -cname -title -threads 100 -timeout 3 -verbose -location -web-server -status-code -tech-detect -content-type -content-length -irh  -u %s -o '
 * enableScreenshot: false
 * secretId: AKIxxxx
 * secretKey: xxx
 * bucketName: xxx
 * regionName: ap-xxx
 * 结果处理类名: ipOrSubDomainOrPortToWebProcessor
 */
public class Httpx implements CommonScriptBody {

    private ILiteFlowMessageSendService messageSendService;

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            messageSendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            messageSendService.INFO("开始httpx web探测...");
            JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
            JSONObject jsonObject = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String resultPath = taskExecuteMessage.getResultPath() + "httpx_" + UUID.randomUUID() + ".json";
            messageSendService.INFO("结果保存路径:{}", resultPath);
            String command = config.getString("command");
            if (config.getBoolean("enableScreenshot")) {
                command = command + " -screenshot -exclude-headless-body";
            }
            Map<String, String> taskList = new HashMap<>();
            switch (taskExecuteMessage.getAssetType()) {
                case "port":
                    // 端口
                    JSONArray domains = jsonObject.getJSONArray("domains");
                    if (domains != null && !domains.isEmpty()) {
                        for (Object domain : domains) {
                            JSONObject object = (JSONObject) domain;
                            String subDomain = object.getString("subDomain");
                            if (StringUtils.isNotBlank(subDomain)) {
                                resultPath = taskExecuteMessage.getResultPath() + "httpx_" + UUID.randomUUID() + ".json";
                                taskList.put(subDomain, String.format(command, subDomain + ":" + jsonObject.getString("port"), resultPath));
                            }
                        }
                    } else {
                        String ip = jsonObject.getString("ip_dictText");
                        taskList.put("", String.format(command, ip + ":" + jsonObject.getString("port"), resultPath));
                    }
                    break;
                case "sub_domain":
                    // 域名
                    String sub_domain = jsonObject.getString("subDomain");
                    taskList.put(sub_domain, String.format(command, sub_domain, resultPath));
                    break;
                case "ip":
                    // ip
                    String ip = jsonObject.getString("ip");
                    taskList.put("", String.format(command, ip, resultPath));
                    break;
                default:
                    messageSendService.ERROR("不支持的资产类型!:{}", taskExecuteMessage.getAssetType());
                    break;
            }
            for (String s : taskList.keySet()) {
                executeTask(s, taskList.get(s), config, resultPath);
            }
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void executeTask(String domain, String task, JSONObject config, String resultPath) {
        try {
            messageSendService.INFO("开始执行Web扫描,命令是:{}", task);
            CommandUtils.CommandResult result = CommandUtils.executeCommand(task);
            if (result.getExitCode() == 0) {
                String resultContent = new String(Files.readAllBytes(Paths.get(resultPath)));
                JSONObject resultJson = JSONObject.parseObject(resultContent);
                if (resultJson != null) {
                    IpOrSubDomainOrPortToWebDTO ipOrSubDomainOrPortToWebDTO = (IpOrSubDomainOrPortToWebDTO) JSONObject.parseObject(resultContent, IpOrSubDomainOrPortToWebDTO.class);
                    ipOrSubDomainOrPortToWebDTO.setDomain(domain);
                    ipOrSubDomainOrPortToWebDTO.setWebUrl(resultJson.getString("url"));
                    ipOrSubDomainOrPortToWebDTO.setHttpSchema(resultJson.getString("scheme"));
                    ipOrSubDomainOrPortToWebDTO.setDelayTime(resultJson.getString("time"));
                    ipOrSubDomainOrPortToWebDTO.setIp(resultJson.getString("host"));
                    ipOrSubDomainOrPortToWebDTO.setWebHeader(resultJson.getString("header"));
                    ipOrSubDomainOrPortToWebDTO.setWebTitle(resultJson.getString("title"));
                    JSONObject hash = resultJson.getJSONObject("hash");
                    ipOrSubDomainOrPortToWebDTO.setBodyMd5(hash.getString("body_md5"));
                    ipOrSubDomainOrPortToWebDTO.setHeaderMd5(hash.getString("header_md5"));
                    ipOrSubDomainOrPortToWebDTO.setSource("httpx");
                    JSONArray tech = resultJson.getJSONArray("tech");
                    if (tech != null) {
                        JSONArray jsonArray = new JSONArray();
                        for (Object o : tech) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", o.toString());
                            jsonArray.add(jsonObject);
                        }
                        ipOrSubDomainOrPortToWebDTO.setTech(JSONObject.toJSONString(jsonArray));
                    }
                    if (config.getBoolean("enableScreenshot")) {
                        String secretId = config.getString("secretId");
                        String secretKey = config.getString("secretKey");
                        String bucketName = config.getString("bucketName");
                        String regionName = config.getString("regionName");
                        String screenshot_path = resultJson.getString("screenshot_path");
                        String screenshot_path_rel = resultJson.getString("screenshot_path_rel");
                        if (screenshot_path != null && screenshot_path_rel != null) {
                            ipOrSubDomainOrPortToWebDTO.setScreenshot(TencentCOSUtils.uploadTencentCOS(secretId, secretKey, bucketName, regionName, screenshot_path, screenshot_path_rel));
                        }
                        String stored_response_path = resultJson.getString("stored_response_path");
                        if (stored_response_path != null) {
                            String path = UUID.randomUUID() + ".txt";
                            messageSendService.INFO("Httpx返回包存储路径：{}", path);
                            ipOrSubDomainOrPortToWebDTO.setResponseBody(TencentCOSUtils.uploadTencentCOS(secretId, secretKey, bucketName, regionName, stored_response_path, path));
                        }
                    } else {
                        messageSendService.INFO("Httpx Web扫描执行完成,结果是:{}", resultJson);
                    }
                    messageSendService.sendResult(ipOrSubDomainOrPortToWebDTO);
                } else {
                    messageSendService.INFO("Httpx Web扫描执行完成,结果为空");
                }
            } else {
                messageSendService.ERROR("执行Httpx Web扫描执行失败,错误码是:{}", result.getExitCode());
            }
        } catch (Exception e) {
            messageSendService.ERROR("执行Httpx Web扫描执行失败,错误:{}", e.getMessage());
        }
    }
}