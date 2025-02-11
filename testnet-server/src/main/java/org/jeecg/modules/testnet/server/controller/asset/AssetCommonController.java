package org.jeecg.modules.testnet.server.controller.asset;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 黑名单
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Api(tags = "资产公共接口")
@RestController
@RequestMapping("/testnet.server/asset/common")
@Slf4j
public class AssetCommonController {

    @Autowired
    private IAssetCommonOptionService assetCommonOptionService;

    @ApiOperation(value = "通过查询删除资产", notes = "通过查询删除资产")
    @PostMapping(value = "/deleteBySearch")
    public Result<String> delete(@RequestBody String params) {
        assetCommonOptionService.deleteAssetByQuery(params);
        return Result.OK("删除成功！");
    }

    @ApiOperation(value = "通过查询修改资产标签", notes = "通过查询修改资产标签")
    @PostMapping(value = "/changeLabels")
    public Result<String> changeLabels(@RequestBody String params) {
        assetCommonOptionService.handleChangeLabels(params);
        return Result.OK("修改标签成功！");
    }

    @ApiOperation(value = "通过查询批量修改漏洞状态", notes = "通过查询批量修改漏洞状态")
    @PostMapping(value = "/changeVulStatus")
    public Result<String> changeVulStatus(@RequestBody String params) {
        assetCommonOptionService.handleChangeVulStatus(params);
        return Result.OK("批量修改漏洞状态成功！");
    }
}
