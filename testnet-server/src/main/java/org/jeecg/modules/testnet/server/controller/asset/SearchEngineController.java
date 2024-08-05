/**
 * @program: JeecgBoot
 * @description:
 * @author: TestNet
 * @create: 2024-07-03
 **/
package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.dto.AssetSearchImportDTO;
import org.jeecg.modules.testnet.server.service.asset.IAssetSearchService;
import org.jeecg.modules.testnet.server.vo.AssetSearchVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "API")
@RestController
@RequestMapping("/testnet.server/asset")
@Slf4j
public class SearchEngineController {

    @Resource
    private IAssetSearchService assetSearchService;

    @ApiOperation(value = "空间搜索引擎", notes = "空间搜索引擎")
    @PostMapping(value = "/search")
    public Result<IPage<AssetSearchVO>> list(@RequestBody AssetSearchDTO assetSearchDTO) {
        return assetSearchService.list(assetSearchDTO);
    }

    @ApiOperation(value = "空间搜索引擎批量导入", notes = "空间搜索引擎批量导入")
    @PostMapping(value = "/import")
    public Result<String> importBatch(@RequestBody AssetSearchImportDTO assetSearchImportDTO) {
        assetSearchService.importAsset(assetSearchImportDTO);
        return Result.OK("导入成功！一键导入请去工作流管理-任务列表查看进度");
    }
}
