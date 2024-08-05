/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-01
 **/
package org.jeecg.modules.testnet.server.service.processer.impl;

import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.testnet.server.service.processer.IAssetResultProcessorService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetResultProcessorServiceImpl implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public List<DictModel> getProcessList() {
        List<DictModel> list = new ArrayList<>();
        String[] beanNames = applicationContext.getBeanNamesForType(IAssetResultProcessorService.class, false, false);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            if (bean instanceof IAssetResultProcessorService) {
                DictModel dictModel = new DictModel(beanName, ((IAssetResultProcessorService) bean).getClass().getSimpleName());
                list.add(dictModel);
            }
        }
        return list;
    }
}
