package script.script;

import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import org.apache.commons.lang.StringUtils;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.AssetUpdateDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.*;

/**
 * 脚本名称：dns解析
 * 通过dns解析判断域名是否有效及是否有CDN
 * 适用资产：子域名
 * 结果处理类名: assetUpdateProcessor
 */
public class DnsResolver implements CommonScriptBody {

    private ILiteFlowMessageSendService messageSendService;

    private boolean isPrivateIP(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] address = inetAddress.getAddress();

            // Check if the IP is IPv4
            if (address.length == 4) {
                int firstOctet = address[0] & 0xFF;
                int secondOctet = address[1] & 0xFF;

                // 10.0.0.0 - 10.255.255.255
                if (firstOctet == 10) {
                    return true;
                }

                // 172.16.0.0 - 172.31.255.255
                if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) {
                    return true;
                }

                // 192.168.0.0 - 192.168.255.255
                if (firstOctet == 192 && secondOctet == 168) {
                    return true;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Void body(ScriptExecuteWrap wrap) {

        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        try {
            messageSendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            messageSendService.setTaskId(taskExecuteMessage.getTaskId());
            messageSendService.INFO("开始dns解析探测...");
            JSONObject jsonObject = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            String subDomain = jsonObject.getString("subDomain");
            Resolver resolver = new SimpleResolver("223.5.5.5");
            Duration timeoutDuration = Duration.ofSeconds(2);
            resolver.setTimeout(timeoutDuration);
            DNSResult result = getDNSRecords(subDomain, resolver);
            boolean isCdn = false;
            boolean success = false;
            if (result.getARecords().length > 0) {
                success = true;
                StringBuilder ips = new StringBuilder();
                for (String ip : result.getARecords()) {
                    messageSendService.INFO("检测到域名:{} A记录：{}", subDomain, ip);
                    if (ips.length() > 0) {
                        ips.append(",");
                    }
                    ips.append(ip);
                }
                jsonObject.put("ips", ips.toString());
                if (result.getARecords().length > 1) {
                    isCdn = true;
                }
            }
            if (result.getCname() != null) {
                success = true;
                isCdn = true;
                messageSendService.INFO("检测到域名:{} CNAME记录：{}", subDomain, result.getCname());
                jsonObject.put("dnsRecord", result.getCname());
            }
            if (isCdn) {
                jsonObject.put("assetLabel", "cdn");
            }
            if (!success) {
                jsonObject.put("assetLabel", "无效资产");
            }
            if (result.getARecords().length == 1 && isPrivateIP(result.getARecords()[0])) {
                jsonObject.put("assetLabel", "内网IP");
            }
            if (StringUtils.isNotBlank(jsonObject.getString("assetLabel"))) {

            }
            messageSendService.INFO("dns解析完成:{}", jsonObject.toString());
            AssetUpdateDTO assetUpdateDTO = new AssetUpdateDTO();
            assetUpdateDTO.setData(jsonObject.toString());
            messageSendService.sendResult(assetUpdateDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public DNSResult getDNSRecords(final String domain, final Resolver resolver) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> cnameTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getCNAMERecord(domain, resolver);
            }
        };

        Callable<String[]> aTask = new Callable<String[]>() {
            @Override
            public String[] call() throws Exception {
                return getARecords(domain, resolver);
            }
        };

        Future<String> cnameFuture = executor.submit(cnameTask);
        Future<String[]> aFuture = executor.submit(aTask);

        String cname = (String) cnameFuture.get();
        String[] aRecords = (String[]) aFuture.get();

        executor.shutdown();

        return new DNSResult(cname, aRecords);
    }

    public String getCNAMERecord(String domain, Resolver resolver) throws Exception {
        Lookup lookup = new Lookup(domain, Type.CNAME);
        lookup.setResolver(resolver);
        lookup.run();
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            Record[] records = lookup.getAnswers();
            return records.length > 0 ? records[0].rdataToString() : null;
        }
        return null;
    }

    public String[] getARecords(String domain, Resolver resolver) throws Exception {
        Lookup lookup = new Lookup(domain, Type.A);
        lookup.setResolver(resolver);
        lookup.run();
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            Record[] records = lookup.getAnswers();
            String[] ips = new String[records.length];
            for (int i = 0; i < records.length; i++) {
                ips[i] = records[i].rdataToString();
            }
            return ips;
        }
        return new String[0];
    }

    private class DNSResult {
        private final String cname;
        private final String[] aRecords;

        public DNSResult(String cname, String[] aRecords) {
            this.cname = cname;
            this.aRecords = aRecords;
        }

        public String getCname() {
            return cname;
        }

        public String[] getARecords() {
            return aRecords;
        }
    }
}