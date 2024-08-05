import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.JaninoCommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.DomainToCompanyDTO;
import testnet.common.entity.HttpResponse;
import testnet.common.entity.liteflow.TaskExecuteMessage;
import testnet.common.utils.HttpUtils;

/**
 * 脚本名称：查询ICP备案信息
 * 适用资产：域名
 * 配置：
 * 结果处理类名: domainToCompanyProcessor
 */
public class ICPApi2 implements JaninoCommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = (TaskExecuteMessage) wrap.cmp.getRequestData();
        ILiteFlowMessageSendService sendService = (ILiteFlowMessageSendService) ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        sendService.setTaskId(taskExecuteMessage.getTaskId());
        sendService.INFO("开始查询ICP备案信息");
        try {
            String domain = instanceParams.getString("domain");
            HttpResponse response = HttpUtils.get("https://abc.com/api/icp/index.php?url=" + domain);
            if (response.getStatusCode() == 200) {
                sendService.INFO("查询备案成功，返回包:{}", response.getBody());
                JSONObject jsonObject = JSONObject.parseObject(response.getBody());
                String company = jsonObject.getString("organizer");
                String icpNumber = jsonObject.getString("licenseKey");
                sendService.INFO("查询到备案信息，公司名称：{},备案号:{}", company, icpNumber);
                DomainToCompanyDTO domainToCompanyDTO = new DomainToCompanyDTO();
                domainToCompanyDTO.setIcpNumber(icpNumber);
                domainToCompanyDTO.setCompanyName(company);
                sendService.sendResult(domainToCompanyDTO);
            } else {
                sendService.ERROR("查询备案失败！请求状态码： {}", response.getStatusCode());
            }
        } catch (Exception e) {
            sendService.ERROR("查询备案失败！ 错误信息：{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}