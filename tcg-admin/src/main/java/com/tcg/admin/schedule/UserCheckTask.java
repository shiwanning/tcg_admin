package com.tcg.admin.schedule;

import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.impl.OperatorLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UserCheckTask {
    private static final Logger logger = LoggerFactory.getLogger(UserCheckTask.class);


    @Autowired
    private OperatorService operatorService;

    @Autowired
    private OperatorLoginService operatorLoginService;
    //在固定时间执行
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 1000 * 60)
    public void reportCurrentByCron(){

        logger.info("Scheduling Tasks - User LastLoginTimeCheck - Start: The time is now " + new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format (new Date()));
        operatorService.updateThirtyNotLoginUser();
        operatorLoginService.removeAllUser();
    }

}
