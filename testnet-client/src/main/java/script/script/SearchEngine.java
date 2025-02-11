import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.body.CommonScriptBody;
import com.yomahub.liteflow.spi.holder.ContextAwareHolder;
import testnet.client.service.ILiteFlowMessageSendService;
import testnet.common.dto.SearchEngineImportDTO;
import testnet.common.entity.liteflow.TaskExecuteMessage;


public class SearchEngine implements CommonScriptBody {
    public Void body(ScriptExecuteWrap wrap) {
        TaskExecuteMessage taskExecuteMessage = wrap.cmp.getRequestData();
        try {
            ILiteFlowMessageSendService sendService = ContextAwareHolder.loadContextAware().getBean(ILiteFlowMessageSendService.class);
            sendService.setTaskId(taskExecuteMessage.getTaskId());
            JSONObject instanceParams = JSONObject.parseObject(taskExecuteMessage.getTaskParams());
            SearchEngineImportDTO searchEngineImportDTO = new SearchEngineImportDTO();
            searchEngineImportDTO.setCurrentPage(instanceParams.getInteger("currentPage"));
            sendService.sendResult(searchEngineImportDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}