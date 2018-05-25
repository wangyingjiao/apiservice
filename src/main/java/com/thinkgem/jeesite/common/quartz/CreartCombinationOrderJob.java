package com.thinkgem.jeesite.common.quartz;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.open.service.SendQuartzCombinationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class CreartCombinationOrderJob implements Job{
	private static SendQuartzCombinationService sendQuartzCombinationService = SpringContextHolder.getBean(SendQuartzCombinationService.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("-----------QuartzJob 开始-----");
		sendQuartzCombinationService.doCreartCombinationOrder();
		System.out.println("-----------QuartzJob 结束-----");
	}
}
