/**
 * @program: jeecg-boot
 * @description:
 * @author: TestNet
 * @create: 2024-06-12
 **/
package org.jeecg.modules.testnet.server.controller.dashboard;

import org.jeecg.modules.testnet.server.service.dashboard.IDashBoardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/testnet.server/dashboard")
public class DashBoardController {

    @Resource
    private IDashBoardService dashBoardService;

    @RequestMapping("/getCardData")
    public String getCardData() {
        return dashBoardService.getCardData();
    }

    @RequestMapping("/getScriptData")
    public String getScriptData() {
        return dashBoardService.getTaskData();
    }

}
