package com.thinkgem.jeesite.common.quartz;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.open.service.SendQuartzService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;


public class JointWaitJob implements Job{
	private static SendQuartzService sendQuartzService = SpringContextHolder.getBean(SendQuartzService.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("-----------QuartzJob 开始-----");
		sendQuartzService.doJointWait();
		System.out.println("-----------QuartzJob 结束-----");
	}
}
