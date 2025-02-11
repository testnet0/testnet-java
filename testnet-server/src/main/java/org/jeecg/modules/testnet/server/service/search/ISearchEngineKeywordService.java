package org.jeecg.modules.testnet.server.service.search;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.SearchEngineKeyword;
import org.jeecg.modules.testnet.server.vo.asset.SearchEngineKeywordVO;

import java.util.List;

/**
 * @Description: 搜索引擎语法
 * @Author: jeecg-boot
 * @Date: 2024-09-12
 * @Version: V1.0
 */
public interface ISearchEngineKeywordService extends IService<SearchEngineKeyword> {

    Result<List<SearchEngineKeywordVO>> autoComplete(AssetSearchDTO assetSearchDTO);
}
