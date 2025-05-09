package org.jeecg.modules.testnet.server.controller.asset;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.AssetPortDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetPort;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetPortServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 端口
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "端口")
@RestController
@RequestMapping("/testnet.server/assetPort")
@Slf4j
public class AssetPortController extends JeecgController<AssetPort, AssetPortServiceImpl> {
    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    /**
     * 分页列表查询
     *
     * @param assetPort
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "端口-分页列表查询")
    @Operation(summary = "端口-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetPort assetPort,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetPort, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.PORT));
    }

    /**
     * 添加
     *
     * @param assetPortDTO
     * @return
     */
    @AutoLog(value = "端口-添加")
    @Operation(summary = "端口-添加")
    @RequiresPermissions("testnet.server:asset_port:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AssetPortDTO assetPortDTO) {
        List<AssetPortDTO> assetPortDTOS = new ArrayList<>();
        for (String s : assetPortDTO.getPortRange().split("\n")) {
            s = s.trim();
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            if (s.contains("-")) {
                // 处理端口范围格式
                String[] range = s.split("-");
                if (range.length == 2) {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int i = start; i <= end; i++) {
                        AssetPortDTO portDTO = new AssetPortDTO();
                        BeanUtils.copyProperties(assetPortDTO, portDTO);
                        portDTO.setPort(i);
                        assetPortDTOS.add(portDTO);
                    }
                }
            } else {
                // 处理单个端口
                AssetPortDTO portDTO = new AssetPortDTO();
                BeanUtils.copyProperties(assetPortDTO, portDTO);
                portDTO.setPort(Integer.parseInt(s));
                assetPortDTOS.add(portDTO);
            }
        }
        Result<?> addResult = assetCommonOptionService.batchAdd(assetPortDTOS, AssetTypeEnums.PORT);
        if (addResult.getCode().equals(200)) {
            return Result.OK("添加成功!");
        } else {
            JSONObject jsonObject = (JSONObject) addResult.getResult();
            return Result.error(jsonObject.getString("errorMessage"));
        }
    }

    /**
     * 编辑
     *
     * @param assetPortDTO
     * @return
     */
    @AutoLog(value = "端口-编辑")
    @Operation(summary = "端口-编辑")
    @RequiresPermissions("testnet.server:asset_port:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetPortDTO assetPortDTO) {
        Result<?> editResult = assetCommonOptionService.updateAssetByType(assetPortDTO, AssetTypeEnums.PORT);
        if (editResult.getCode().equals(200)) {
            return Result.OK("编辑成功!");
        }
        return Result.error(editResult.getCode(), editResult.getMessage());
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "端口-通过id删除")
    @Operation(summary = "端口-通过id删除")
    @RequiresPermissions("testnet.server:asset_port:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.PORT);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "端口-批量删除")
    @Operation(summary = "端口-批量删除")
    @RequiresPermissions("testnet.server:asset_port:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.PORT);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "端口-通过id查询")
    @Operation(summary = "端口-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<? extends AssetBase> queryById(@RequestParam(name = "id", required = true) String id) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, AssetTypeEnums.PORT);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetPort
     */
    @RequiresPermissions("testnet.server:asset_port:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetPort assetPort) {
        // return super.exportXlsSheet(request, assetPort, AssetPort.class, "端口", null, 500000);
        return super.exportXls(request, assetPort, AssetPort.class, "端口");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_port:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return assetCommonOptionService.importExcel(request, response, AssetPortDTO.class, AssetTypeEnums.PORT);
    }

}
