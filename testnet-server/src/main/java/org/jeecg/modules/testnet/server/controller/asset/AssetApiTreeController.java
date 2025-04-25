package org.jeecg.modules.testnet.server.controller.asset;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.modules.testnet.server.entity.asset.AssetApiTree;
import org.jeecg.modules.testnet.server.service.asset.IAssetApiTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: API
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "API")
@RestController
@RequestMapping("/testnet.server/assetApiTree")
@Slf4j
public class AssetApiTreeController extends JeecgController<AssetApiTree, IAssetApiTreeService> {
    @Autowired
    private IAssetApiTreeService assetApiTreeService;


    /**
     * 【vue3专用】加载节点的子数据
     *
     * @param keyword 关键字
     * @return
     */
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
    public Result<IPage<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize, @RequestParam(name = "keyword", required = false) String keyword, @RequestParam(name = "id", required = false) String id) {
        Result<IPage<SelectTreeModel>> result = new Result<>();
        try {
            result.setResult(assetApiTreeService.getRootTree(keyword, id, pageNo, pageSize));
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
    public Result<List<SelectTreeModel>> loadTreeChildren(@RequestParam(name = "pid", required = false) String parentId) {
        Result<List<SelectTreeModel>> result = new Result<>();
        try {
            List<SelectTreeModel> ls = assetApiTreeService.getChildTree(parentId);
            result.setResult(ls);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "API-批量删除")
    @Operation(summary = "API-批量删除")
    @RequiresPermissions("testnet.server:asset_api:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetApiTreeService.del(ids);
        return Result.OK("批量删除成功！");
    }
}
