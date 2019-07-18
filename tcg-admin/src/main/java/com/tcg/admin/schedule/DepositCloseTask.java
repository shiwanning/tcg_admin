package com.tcg.admin.schedule;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.tcg.admin.service.TaskService;

@Component
public class DepositCloseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositCloseTask.class);
    
    @Autowired
    private TaskService taskService;
    
    @Scheduled(cron = "0 15 0 * * ?")   
    public void execute() {
        LOGGER.info("close deposit task start.");
        closeDeposit(7);
        LOGGER.info("close deposit task end.");
    }

    private void closeDeposit(int days) {
        Date ednDate = DateUtils.truncate(DateUtils.addDays(new Date(), -days), Calendar.DATE);
        taskService.closeTask(ednDate, Lists.newArrayList("DEP", "UCD"));
    }
    
}
