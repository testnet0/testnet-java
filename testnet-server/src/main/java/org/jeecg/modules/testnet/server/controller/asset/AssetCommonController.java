package org.jeecg.modules.testnet.server.controller.asset;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.vo.AssetApiVO;
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
    public Result<IPage<AssetApiVO>> list(@RequestBody String params) {
        assetCommonOptionService.deleteAssetByQuery(params);
        return Result.OK("删除成功！");
    }
}
