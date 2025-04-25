package org.jeecg.modules.testnet.server.controller.asset;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetBase;
import org.jeecg.modules.testnet.server.entity.asset.AssetIp;
import org.jeecg.modules.testnet.server.service.asset.IAssetCommonOptionService;
import org.jeecg.modules.testnet.server.service.asset.IAssetIpSubdomainRelationService;
import org.jeecg.modules.testnet.server.service.asset.impl.AssetIpServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import testnet.common.enums.AssetTypeEnums;
import testnet.common.utils.IpUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: ip
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
@Tag(name = "ip")
@RestController
@RequestMapping("/testnet.server/assetIp")
@Slf4j
public class AssetIpController extends JeecgController<AssetIp, AssetIpServiceImpl> {


    @Resource
    private IAssetCommonOptionService assetCommonOptionService;

    @Resource
    private IAssetIpSubdomainRelationService assetIpSubdomainRelationService;

    /**
     * 分页列表查询
     *
     * @param assetIp
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "ip-分页列表查询")
    @Operation(summary = "ip-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<? extends AssetBase>> queryPageList(AssetIp assetIp, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        return Result.OK(assetCommonOptionService.page(assetIp, pageNo, pageSize, req.getParameterMap(), AssetTypeEnums.IP));
    }

    /**
     * 添加
     *
     * @param assetIp
     * @return
     */
    @AutoLog(value = "ip-添加")
    @Operation(summary = "ip-添加")
    @RequiresPermissions("testnet.server:asset_ip:add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody AssetIpDTO assetIp) {
        List<AssetIpDTO> assetIpList = new ArrayList<>();
        for (String s : assetIp.getIp().split("\n")) {
            s = s.trim();
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            if (s.contains("/")) {
                // 处理CIDR格式的IP段
                for (String ip : IpUtils.cidrToIPList(s)) {
                    assetIpList.add(createAssetIpDTO(assetIp, ip));
                }
            } else if (s.contains("-")) {
                // 处理IP范围格式
                String[] range = s.split("-");
                if (range.length == 2) {
                    String startIp = range[0].trim();
                    String endIp = range[1].trim();
                    for (String ip : IpUtils.rangeToIPList(startIp, endIp)) {
                        assetIpList.add(createAssetIpDTO(assetIp, ip));
                    }
                }
            } else {
                // 处理单个IP
                assetIpList.add(createAssetIpDTO(assetIp, s));
            }
        }
        Result<?> addResult = assetCommonOptionService.batchAdd(assetIpList, AssetTypeEnums.IP);
        if (addResult.getCode().equals(200)) {
            return Result.OK("添加成功!");
        } else {
            JSONObject jsonObject = (JSONObject) addResult.getResult();
            return Result.error(jsonObject.getString("errorMessage"));
        }
    }

    /**
     * 创建并初始化AssetIpDTO对象
     *
     * @param assetIp 源对象
     * @param ip      IP地址
     * @return 初始化后的AssetIpDTO对象
     */
    private AssetIpDTO createAssetIpDTO(AssetIp assetIp, String ip) {
        AssetIpDTO assetIpDTO = new AssetIpDTO();
        BeanUtil.copyProperties(assetIp, assetIpDTO); // 复制属性
        assetIpDTO.setIp(ip); // 设置IP地址
        return assetIpDTO;
    }

    /**
     * 编辑
     *
     * @param assetIp
     * @return
     */
    @AutoLog(value = "ip-编辑")
    @Operation(summary = "ip-编辑")
    @RequiresPermissions("testnet.server:asset_ip:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AssetIpDTO assetIp) {
        assetIpSubdomainRelationService.delByAssetIpId(assetIp.getId());
        Result<?> editResult = assetCommonOptionService.updateAssetByType(assetIp, AssetTypeEnums.IP);
        if (editResult.getCode().equals(200)) {
            return Result.OK("编辑成功!");
        } else {
            JSONObject jsonObject = (JSONObject) editResult.getResult();
            return Result.error(jsonObject.getString("errorMessage"));
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "ip-通过id删除")
    @Operation(summary = "ip-通过id删除")
    @RequiresPermissions("testnet.server:asset_ip:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        assetCommonOptionService.delByIdAndAssetType(id, AssetTypeEnums.IP);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "ip-批量删除")
    @Operation(summary = "ip-批量删除")
    @RequiresPermissions("testnet.server:asset_ip:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        assetCommonOptionService.delByIdAndAssetType(ids, AssetTypeEnums.IP);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "ip-通过id查询")
    @Operation(summary = "ip-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<? extends AssetBase> queryById(@RequestParam(name = "id", required = true) String id) {
        return assetCommonOptionService.getAssetDOByIdAndAssetType(id, AssetTypeEnums.IP);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param assetIp
     */
    @RequiresPermissions("testnet.server:asset_ip:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AssetIp assetIp) {
       // return super.exportXlsSheet(request, assetIp, AssetIp.class, "ip", null, 50000);
         return super.exportXls(request, assetIp, AssetIp.class, "ip");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("testnet.server:asset_ip:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return assetCommonOptionService.importExcel(request, response, AssetIpDTO.class, AssetTypeEnums.IP);
    }

}
