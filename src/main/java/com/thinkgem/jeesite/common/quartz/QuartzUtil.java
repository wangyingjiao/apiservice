package com.thinkgem.jeesite.common.quartz;

import com.thinkgem.jeesite.open.service.SendQuartzService;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class QuartzUtil {

    public QuartzUtil(){
        System.out.println("------QuartzUtil 初始化-----------");
    }

    public void doJointWait() throws Exception{
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        // jobs可以在scheduled的sched.start()方法前被调用
        JobDetail job = newJob(JointWaitJob.class).withIdentity("job1", "jointwait").build();
        CronTrigger trigger = newTrigger().withIdentity("trigger1", "jointwait").withSchedule(cronSchedule("10 0/2 * * * ?")).build();
        Date ft = sched.scheduleJob(job, trigger);

        // 开始执行，start()方法被调用后，计时器就开始工作，计时调度中允许放入N个Job
        sched.start();
    }

    public void doCreartCombinationOrder() throws Exception{
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        // jobs可以在scheduled的sched.start()方法前被调用
        JobDetail job = newJob(CreartCombinationOrderJob.class).withIdentity("job1", "combination").build();
        CronTrigger trigger = newTrigger().withIdentity("trigger1", "combination").withSchedule(cronSchedule("0 0 18 ? * SUN")).build();
        Date ft = sched.scheduleJob(job, trigger);

        // 开始执行，start()方法被调用后，计时器就开始工作，计时调度中允许放入N个Job
        sched.start();
    }
}
