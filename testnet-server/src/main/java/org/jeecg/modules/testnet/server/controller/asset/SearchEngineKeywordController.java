package org.jeecg.modules.testnet.server.controller.asset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.SearchEngineKeyword;
import org.jeecg.modules.testnet.server.service.search.ISearchEngineKeywordService;
import org.jeecg.modules.testnet.server.vo.asset.SearchEngineKeywordVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 搜索引擎语法
 * @Author: jeecg-boot
 * @Date:   2024-09-12
 * @Version: V1.0
 */
@Api(tags="搜索引擎语法")
@RestController
@RequestMapping("/testnet/searchEngineKeyword")
@Slf4j
public class SearchEngineKeywordController extends JeecgController<SearchEngineKeyword, ISearchEngineKeywordService> {
	@Autowired
	private ISearchEngineKeywordService searchEngineKeywordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param searchEngineKeyword
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "搜索引擎语法-分页列表查询")
	@ApiOperation(value="搜索引擎语法-分页列表查询", notes="搜索引擎语法-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<SearchEngineKeyword>> queryPageList(SearchEngineKeyword searchEngineKeyword,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<SearchEngineKeyword> queryWrapper = QueryGenerator.initQueryWrapper(searchEngineKeyword, req.getParameterMap());
		Page<SearchEngineKeyword> page = new Page<SearchEngineKeyword>(pageNo, pageSize);
		IPage<SearchEngineKeyword> pageList = searchEngineKeywordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param searchEngineKeyword
	 * @return
	 */
	@AutoLog(value = "搜索引擎语法-添加")
	@ApiOperation(value="搜索引擎语法-添加", notes="搜索引擎语法-添加")
	@RequiresPermissions("testnet:search_engine_keyword:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SearchEngineKeyword searchEngineKeyword) {
		searchEngineKeywordService.save(searchEngineKeyword);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param searchEngineKeyword
	 * @return
	 */
	@AutoLog(value = "搜索引擎语法-编辑")
	@ApiOperation(value="搜索引擎语法-编辑", notes="搜索引擎语法-编辑")
	@RequiresPermissions("testnet:search_engine_keyword:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SearchEngineKeyword searchEngineKeyword) {
		searchEngineKeywordService.updateById(searchEngineKeyword);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "搜索引擎语法-通过id删除")
	@ApiOperation(value="搜索引擎语法-通过id删除", notes="搜索引擎语法-通过id删除")
	@RequiresPermissions("testnet:search_engine_keyword:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		searchEngineKeywordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "搜索引擎语法-批量删除")
	@ApiOperation(value="搜索引擎语法-批量删除", notes="搜索引擎语法-批量删除")
	@RequiresPermissions("testnet:search_engine_keyword:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.searchEngineKeywordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "搜索引擎语法-通过id查询")
	@ApiOperation(value="搜索引擎语法-通过id查询", notes="搜索引擎语法-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SearchEngineKeyword> queryById(@RequestParam(name="id",required=true) String id) {
		SearchEngineKeyword searchEngineKeyword = searchEngineKeywordService.getById(id);
		if(searchEngineKeyword==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(searchEngineKeyword);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param searchEngineKeyword
    */
    @RequiresPermissions("testnet:search_engine_keyword:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SearchEngineKeyword searchEngineKeyword) {
        return super.exportXls(request, searchEngineKeyword, SearchEngineKeyword.class, "搜索引擎语法");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("testnet:search_engine_keyword:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SearchEngineKeyword.class);
    }

	 @ApiOperation(value = "空间搜索引擎语法提示", notes = "空间搜索引擎语法提示")
	 @PostMapping(value = "/autoComplete")
	 public Result<List<SearchEngineKeywordVO>> autoComplete(@RequestBody AssetSearchDTO assetSearchDTO) {
		 return searchEngineKeywordService.autoComplete(assetSearchDTO);
	 }

}
