import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
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
public class ICPApi2 implements CommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
        JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
        sendService.setTaskId(taskExecuteMessage.getTaskId());
        sendService.INFO("开始查询ICP备案信息");

        try {
            Thread.sleep(10000);
            String domain = instanceParams.getString("domain");
            // 调用新的 ICP 查询接口
            HttpResponse response = HttpUtils.get("https://api.leafone.cn/api/icp?name="+domain);

            if (response.getStatusCode() == 200) {
                sendService.INFO("返回包: {}", response.getBody());
                JSONObject responseBody = JSONObject.parseObject(response.getBody());
                if (responseBody.getInteger("code") == 200) {
                    JSONObject data = responseBody.getJSONObject("data");
                    String companyName = data.getJSONArray("list").getJSONObject(0).getString("unitName");
                    String icpNumber = data.getJSONArray("list").getJSONObject(0).getString("mainLicence");
                    DomainToCompanyDTO domainToCompanyDTO = new DomainToCompanyDTO();
                    domainToCompanyDTO.setIcpNumber(icpNumber);
                    domainToCompanyDTO.setCompanyName(companyName);
                    sendService.sendResult(domainToCompanyDTO);
                } else {
                    sendService.ERROR("查询备案失败！返回错误信息：{}", responseBody.getString("msg"));
                }
            } else {
                sendService.ERROR("查询备案失败！请求状态码：{}", response.getStatusCode());
            }
        } catch (Exception e) {
            sendService.ERROR("查询备案失败！ 错误信息：{}", e.getMessage());
            throw new RuntimeException("ICP查询接口调用失败", e);
        }
        return null;
    }
}
