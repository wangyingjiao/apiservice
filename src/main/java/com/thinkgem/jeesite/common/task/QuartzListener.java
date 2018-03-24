package com.thinkgem.jeesite.common.task;

import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Administrator on 2017/5/19.
 */
public class QuartzListener implements ServletContextListener
{
    private List<Thread> threadList;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(QuartzListener.class);

    public void contextInitialized(ServletContextEvent sce)
    {
        logger.info("休息5秒等待spring注入");
        try
        {
            sleep(5 * 1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        threadList = (List<Thread> )ctx.getBean("threadList");

        if (threadList != null && threadList.size() > 0)
        {
            for (Thread thread : threadList)
            {
                if (thread != null)
                {
                    thread.start();
                    logger.info("启动线程:" + thread.getName());
                }
                else
                {
                    logger.info("启动线程为null！");
                }
            }
        }
        else
        {
            logger.info("启动线程队列为空！");
        }
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
        if (threadList != null && threadList.size() > 0)
        {
            for (Thread t : threadList)
            {
                try
                {
                    if (t.isAlive())
                    {
                        t.interrupt();
                    }
                }
                catch (Exception e)
                {
                    logger.info("停止线程" + t.getName() + "异常！", e);
                }
            }
            threadList.clear();
        }
    }
}