package org.jeecg.modules.testnet.server.mapper.liteflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.testnet.server.entity.liteflow.Chain;


/**
 * @Description: 流程管理
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface ChainMapper extends BaseMapper<Chain> {

    Chain selectProcessorClassNameById(String id);
}
