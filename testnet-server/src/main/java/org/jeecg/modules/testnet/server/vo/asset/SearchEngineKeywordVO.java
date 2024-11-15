package org.jeecg.modules.testnet.server.vo.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jeecg.modules.testnet.server.entity.asset.SearchEngineKeyword;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchEngineKeywordVO {
    private String type;
    private List<SearchEngineKeyword> keywordList;
}
