package com.thinkgem.jeesite.common.task;

import com.thinkgem.jeesite.open.service.SendQuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class OpenSendTask extends Thread
{
    private static Logger logger = LoggerFactory.getLogger(OpenSendTask.class);

    @Resource
    private SendQuartzService sendQuartzService;

    @Override
    public void run()
    {
        logger.info("定时任务线程开始！");
        while (!interrupted())
        {
            try
            {
                sendQuartzService.doJointWait();
            }
            catch (Exception e)
            {
                logger.info("定时任务异常",e);
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////
            try
            {
                sleep(1 * 60 * 1000);//每60秒执行一次
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        logger.info("定时任务线程退出！");
    }
}