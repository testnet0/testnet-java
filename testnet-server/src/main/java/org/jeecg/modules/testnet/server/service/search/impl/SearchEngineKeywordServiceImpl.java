package org.jeecg.modules.testnet.server.service.search.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.testnet.server.dto.AssetSearchDTO;
import org.jeecg.modules.testnet.server.entity.asset.SearchEngineKeyword;
import org.jeecg.modules.testnet.server.mapper.asset.SearchEngineKeywordMapper;
import org.jeecg.modules.testnet.server.service.search.ISearchEngineKeywordService;
import org.jeecg.modules.testnet.server.vo.asset.SearchEngineKeywordVO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: 搜索引擎语法
 * @Author: jeecg-boot
 * @Date: 2024-09-12
 * @Version: V1.0
 */
@Service
public class SearchEngineKeywordServiceImpl extends ServiceImpl<SearchEngineKeywordMapper, SearchEngineKeyword> implements ISearchEngineKeywordService {

    public Result<List<SearchEngineKeywordVO>> autoComplete(AssetSearchDTO assetSearchDTO) {
        QueryWrapper<SearchEngineKeyword> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("engine", assetSearchDTO.getEngine());
        String lastKeyword = extractLastKeywordAfter(assetSearchDTO.getKeyword());
        if (lastKeyword.isEmpty()) {
            lastKeyword = assetSearchDTO.getKeyword(); // 如果没有提取到最后的关键词，则使用原始关键词
        }
        queryWrapper.like("keyword", lastKeyword);
        List<SearchEngineKeyword> list = this.list(queryWrapper);
        List<SearchEngineKeyword> systemKeywords = new ArrayList<>();
        List<SearchEngineKeyword> userKeywords = new ArrayList<>();
        for (SearchEngineKeyword item : list) {
            String inputKeyword = assetSearchDTO.getKeyword();
            int lastKeywordIndex = inputKeyword.lastIndexOf(lastKeyword);
            String combinedKeyword = inputKeyword.substring(0, lastKeywordIndex) + item.getKeyword();
            item.setKeyword(combinedKeyword);
            if (item.getType().equals("sys")) {
                systemKeywords.add(item);
            } else {
                userKeywords.add(item);
            }
        }
        List<SearchEngineKeywordVO> keywords = new ArrayList<>();
        if (!systemKeywords.isEmpty()) {
            keywords.add(new SearchEngineKeywordVO("系统", systemKeywords));
        }
        if (!userKeywords.isEmpty()) {
            keywords.add(new SearchEngineKeywordVO("用户", userKeywords));
        }
        return Result.OK(keywords);
    }

    /**
     * 从输入字符串中提取最后的关键词，考虑 "&&", "||", 和 "and" 作为分隔符
     *
     * @param input 输入的查询字符串
     * @return 最后一个关键词
     */
    public static String extractLastKeywordAfter(String input) {
        // 定义正则表达式，匹配最后一个 "&&", "||", 或 "and" 后面的字符
        String regex = ".*(?:&&|\\|\\||and|or)\\s*(.*)";  // 非捕获组匹配到最后一个分隔符
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // 如果匹配成功，提取最后的关键词
        if (matcher.matches()) {
            return matcher.group(1).trim();  // 提取分隔符后的部分作为最后的关键词
        }

        // 如果没有分隔符，则返回整个输入
        return input;
    }

}
