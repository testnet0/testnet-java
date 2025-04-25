/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.dashboard.impl;

import com.alibaba.fastjson2.JSON;
import org.jeecg.modules.testnet.server.entity.dashboard.StatisticsCard;
import org.jeecg.modules.testnet.server.entity.dashboard.ToDoCard;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.client.IClientConfigService;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.dashboard.IDashBoardService;
import org.jeecg.modules.testnet.server.service.liteflow.IChainService;
import org.jeecg.modules.testnet.server.service.liteflow.ILiteFlowSubTaskService;
import org.jeecg.modules.testnet.server.service.liteflow.IScriptService;
import org.springframework.stereotype.Service;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.enums.LiteFlowStatusEnums;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashBoardServiceImpl implements IDashBoardService {


    @Resource
    private IScriptService scriptService;

    @Resource
    private IChainService chainService;

    @Resource
    private ILiteFlowSubTaskService liteFlowSubTaskService;


    @Resource
    private IClientService clientService;

    @Resource
    private IClientConfigService clientConfigService;

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;


    @Override
    public String getCardData(String projectId) {
        List<ToDoCard> toDoCardList = new ArrayList<>();

        for (AssetTypeEnums value : AssetTypeEnums.values()) {
            long count = assetCommonOptionService.getCountByDate(value, projectId);
            ToDoCard toDocard = new ToDoCard();
            toDocard.setTitle(value.getDescription());
            toDocard.setTotalCount(assetCommonOptionService.getAllCountByAssetType(value, projectId));
            toDocard.setTodayIncreaseCount(count);
            toDoCardList.add(toDocard);
        }
        return JSON.toJSONString(toDoCardList);
    }

    @Override
    public String getTaskData() {
        List<StatisticsCard> statisticsCardList = new ArrayList<>();
        statisticsCardList.add(getInstanceData());
        statisticsCardList.add(getClientData());
        statisticsCardList.add(getScriptData());
        statisticsCardList.add(getWorkFlowData());
        statisticsCardList.add(getClientConfigData());
        return JSON.toJSONString(statisticsCardList);
    }

    private StatisticsCard getScriptData() {
        StatisticsCard statisticsCard = new StatisticsCard();
        statisticsCard.setCount(scriptService.count());
        return statisticsCard;
    }

    private StatisticsCard getWorkFlowData() {
        StatisticsCard statisticsCard = new StatisticsCard();
        statisticsCard.setCount(chainService.count());
        return statisticsCard;
    }

    private StatisticsCard getInstanceData() {
        StatisticsCard statisticsCard = new StatisticsCard();
        statisticsCard.setCount(liteFlowSubTaskService.count());
        statisticsCard.setDesc("完成：" + liteFlowSubTaskService.getCountByStatus(LiteFlowStatusEnums.SUCCEED.name()) + "， 失败：" + liteFlowSubTaskService.getCountByStatus(LiteFlowStatusEnums.FAILED.name()));
        return statisticsCard;
    }

    public StatisticsCard getClientData() {
        StatisticsCard statisticsCard = new StatisticsCard();
        statisticsCard.setCount(clientService.count());
        statisticsCard.setDesc("在线：" + clientService.getOnlineClientsCount() + "， 离线：" + clientService.getOfflineClientsCount());
        return statisticsCard;
    }

    public StatisticsCard getClientConfigData() {
        StatisticsCard statisticsCard = new StatisticsCard();
        statisticsCard.setCount(clientConfigService.count());
        return statisticsCard;
    }
}
