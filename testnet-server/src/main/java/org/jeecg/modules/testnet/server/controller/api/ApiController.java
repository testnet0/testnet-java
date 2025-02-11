/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-24
 **/
package org.jeecg.modules.testnet.server.controller.api;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.client.Client;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.client.IClientService;
import org.jeecg.modules.testnet.server.service.liteflow.impl.BatchRunChain;
import org.jeecg.modules.testnet.server.vo.ApiAddVO;
import org.jeecg.modules.testnet.server.vo.ApiListVO;
import org.jeecg.modules.testnet.server.vo.RunChainVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testnet.server/api")
public class ApiController {

    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private BatchRunChain batchRunChain;

    @Resource
    private IClientService clientService;

    @Value("${testnet.api.token}")
    private String token;

    @SuppressWarnings("unchecked")
    @IgnoreAuth
    @PostMapping(value = "/list")
    public <T extends AssetBase> Result<IPage<? extends AssetBase>> queryAssetByApi(@RequestBody ApiListVO apiListVO) {
        if (apiListVO.getToken() != null && apiListVO.getToken().equals(token)) {
            AssetTypeEnums assetTypeEnums = AssetTypeEnums.fromCode(apiListVO.getAssetType());
            Class<T> assetClass = (Class<T>) assetCommonOptionService.getAssetClassByType(assetTypeEnums);
            if (assetClass == null) {
                return Result.error("未找到该资产类型");
            } else {
                JSONObject query = apiListVO.getQueryParam();
                Map<String, String[]> queryMap = new HashMap<>();
                if (query != null) {
                    query.forEach((k, v) -> {
                        if (v instanceof JSONArray) {
                            JSONArray jsonArray1 = (JSONArray) v;
                            List<String> list = new ArrayList<>();
                            jsonArray1.forEach(o -> list.add(o.toString()));
                            queryMap.put(k, list.toArray(new String[0]));
                        } else if (v instanceof String) {
                            queryMap.put(k, new String[]{v.toString()});
                        }
                    });
                }
                if (apiListVO.getParams() != null) {
                    T asset = apiListVO.getParams().toJavaObject(assetClass);
                    if (asset != null) {
                        return Result.OK(assetCommonOptionService.page(asset, apiListVO.getPageNo(), apiListVO.getPageSize(), queryMap, assetTypeEnums));
                    }
                }
                try {
                    return Result.OK(assetCommonOptionService.page(assetClass.getDeclaredConstructor().newInstance(), apiListVO.getPageNo(), apiListVO.getPageSize(), queryMap, assetTypeEnums));
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    return Result.error(e.getMessage());
                }

            }
        } else {
            return Result.error("token错误");
        }
    }


    @SuppressWarnings("unchecked")
    @IgnoreAuth
    @PostMapping(value = "/add")
    public <T extends AssetBase> Result<String> addAssetByApi(@RequestBody ApiAddVO apiAddVO) {
        if (apiAddVO.getToken() != null && apiAddVO.getToken().equals(token)) {
            AssetTypeEnums assetTypeEnums = AssetTypeEnums.fromCode(apiAddVO.getAssetType());
            Class<T> assetClass = (Class<T>) assetCommonOptionService.getAssetDTOClassByType(assetTypeEnums);
            if (assetClass == null) {
                return Result.error("未找到该资产类型");
            } else {
                if (apiAddVO.getAssetList() != null && !apiAddVO.getAssetList().isEmpty()) {
                    List<T> assetObjects = apiAddVO.getAssetList().toJavaList(assetClass);
                    assetObjects.forEach(assetObject -> assetCommonOptionService.addOrUpdate(assetObject, assetTypeEnums));
                }
                return Result.OK("添加成功");
            }
        } else {
            return Result.error(403, "token错误");
        }
    }

    @IgnoreAuth
    @PostMapping(value = "/runChain")
    public Result<String> batchRunChainByApi(@RequestBody RunChainVO runChainVO) {
        if (runChainVO.getToken() != null && runChainVO.getToken().equals(token)) {
            List<Client> clients = clientService.getAllOnlineClients();
            if (clients == null || clients.isEmpty()) {
                return Result.error("批量扫描任务创建失败,没有在线的客户端！");
            }
            batchRunChain.batchRun(runChainVO.getParams(), 1);
            return Result.OK("任务执行成功，请去工作流管理-任务列表查看任务状态");
        } else {
            return Result.error(403, "token错误");
        }
    }

    @IgnoreAuth
    @PostMapping(value = "/delete")
    public <T extends AssetBase> Result<IPage<? extends AssetBase>> delAssetByApi(@RequestBody ApiListVO apiListVO) {
        if (apiListVO.getToken() != null && apiListVO.getToken().equals(token)) {
            JSONArray ids = apiListVO.getParams().getJSONArray("ids");
            for (Object id : ids) {
                assetCommonOptionService.delByIdAndAssetType(id.toString(), AssetTypeEnums.fromCode(apiListVO.getAssetType()));
            }
            return Result.OK("删除成功！");
        } else {
            return Result.error("token错误");
        }
    }

}
