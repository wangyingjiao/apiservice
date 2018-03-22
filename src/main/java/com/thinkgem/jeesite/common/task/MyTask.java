package com.thinkgem.jeesite.common.task;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.open.service.SendQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

@Service
@Lazy(false)
public class MyTask{

    @Autowired
    SendQuartzService sendQuartzService;

    @Scheduled(cron="10 0/1 *  * * ? ")
    public void aTask(){
        System.out.println("*********定时任务对接开始");
        sendQuartzService.doJointWait();
    }
}
