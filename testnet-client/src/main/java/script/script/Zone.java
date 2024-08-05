import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.CompanyToDomainsDTO;
import testnet.common.entity.HttpResponse;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone implements JaninoCommonScriptBody {

    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        JSONObject config = JSONObject.parseObject(taskExecuteMessage.getConfig());
        String key = config.getString("zone_key");
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        ILiteFlowMessageSendService sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
        sendService.setTaskId(taskExecuteMessage.getTaskId());
        sendService.INFO("开始执行0.zone,参数是: {}", taskExecuteMessage.getTaskParams());
        String componentName = instanceParams.getString("companyName");
        int startPage = 1;
        int pageSize = 100;
        JSONObject result = query(componentName, key, startPage, pageSize);
        List<CompanyToDomainsDTO.Domain> domainList = new ArrayList<>();
        CompanyToDomainsDTO companyToDomainsDTO = new CompanyToDomainsDTO();
        if (result != null && result.getInteger("code") == 0) {
            int totalPages = (result.getInteger("total") + pageSize - 1) / pageSize;
            sendService.INFO("结果总数量: {}，分页数量:{}", result.getInteger("total"), totalPages);

            // 处理第1页数据
            sendService.INFO("正在查询第 1 页...");
            domainList.addAll(processResult(result.getJSONArray("data")));
            sendService.INFO("查询结果: {}", result);
            sendService.INFO("API剩余额度: {}", result.getJSONObject("today_api_search_count"));

            for (int currentPage = 2; currentPage <= totalPages; currentPage++) {
                try {
                    Thread.sleep(1000L); // 暂停1秒
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                result = query(componentName, key, currentPage, pageSize);
                if (result == null || result.getInteger("code") != 0) {
                    sendService.ERROR("查询失败,返回结果: {}", result);
                    break;
                }
                sendService.INFO("正在查询第 {} 页...", currentPage);
                sendService.INFO("查询结果: {}", result);
                sendService.INFO("API剩余额度: {}", result.getJSONObject("today_api_search_count"));
                domainList.addAll(processResult(result.getJSONArray("data")));
            }
        }
        companyToDomainsDTO.setDomainList(domainList);
        sendService.sendResult(companyToDomainsDTO);
        return null;
    }

    private List<CompanyToDomainsDTO.Domain> processResult(JSONArray jsonArray) {
        List<CompanyToDomainsDTO.Domain> domainList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject domain = (JSONObject) o;
            CompanyToDomainsDTO.Domain domain1 = new CompanyToDomainsDTO.Domain();
            domain1.setDomain(domain.getString("domain"));
            domain1.setIcp(domain.getString("icp"));
            domainList.add(domain1);
        }
        return domainList;
    }

    private JSONObject query(String companyName, String key, int page, int pageSize) {
        try {
            String query = String.format("company=%s&&level=根域", companyName);
            String postEndpoint = "https://0.zone/api/data/";
            JSONObject body = new JSONObject();
            body.put("query", query);
            body.put("query_type", "domain");
            body.put("page", page);
            body.put("pagesize", pageSize);
            body.put("zone_key_id", key);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            HttpResponse response = HttpUtils.post(postEndpoint, headers, body.toJSONString());
            if (response.getStatusCode() == 200) {
                return JSON.parseObject(response.getBody());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}