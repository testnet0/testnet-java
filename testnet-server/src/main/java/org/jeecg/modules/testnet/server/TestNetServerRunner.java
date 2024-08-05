package org.jeecg.modules.testnet.server;


import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.es.JeecgElasticsearchTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import testnet.common.constan.Constants;

import javax.annotation.Resource;

@Component
@Slf4j
public class TestNetServerRunner implements ApplicationRunner, DisposableBean {
    @Resource
    private JeecgElasticsearchTemplate jeecgElasticsearchTemplate;


    @Override
    public void run(ApplicationArguments args) {
        // 创建ES索引
        try {
            jeecgElasticsearchTemplate.createIndex(Constants.ES_LOG_INDEX);
            jeecgElasticsearchTemplate.createIndex(Constants.ES_WEB_INDEX);
        } catch (Exception e) {
            log.info("创建ES索引失败!{}", e.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}