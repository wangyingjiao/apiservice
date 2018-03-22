package com.thinkgem.jeesite.common.quartz;

import com.thinkgem.jeesite.open.service.SendQuartzService;
import org.springframework.beans.factory.annotation.Autowired;

public class QuartzUtil {

    @Autowired
    SendQuartzService sendQuartzService;

    public QuartzUtil(){
        System.out.println("计划任务被初始化了");
    }

    public void doJointWait(){
        System.out.println("定时任务对接开始");
        sendQuartzService.doJointWait();
    }

}
