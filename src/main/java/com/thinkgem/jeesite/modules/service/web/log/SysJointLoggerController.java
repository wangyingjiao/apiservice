package com.thinkgem.jeesite.modules.service.web.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.modules.service.entity.log.SysJointLogger;
import com.thinkgem.jeesite.modules.service.service.log.SysJointLoggerService;

import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: SysJointLogController
 * @Description: TODO
 * @author WYR
 * @date 2018年1月31日 下午3:18:36
 * 
 */
@Controller
@RequestMapping(value = "${adminPath}/service/log/SysJointLogger")
public class SysJointLoggerController {
	@Autowired
	private SysJointLoggerService sysJointLoggerService;

	@ResponseBody
	// @RequiresPermissions("log_view")
	@RequestMapping(value = "/listSysJointLogger", method = { RequestMethod.POST, RequestMethod.GET })
	@ApiOperation("获取对接日志记录表")
	public Result listData(@RequestBody(required = false) SysJointLogger sysJointLogger, HttpServletRequest request,
			HttpServletResponse response) {
		if (sysJointLogger == null) {
			sysJointLogger = new SysJointLogger();
		}
		Page<SysJointLogger> serSortInfoPage = new Page<>(request, response);
		Page<SysJointLogger> page = sysJointLoggerService.findPage(serSortInfoPage, sysJointLogger);
		return new SuccResult(page);
	}


	@ResponseBody
	@RequestMapping(value = "doOpenSend", method = {RequestMethod.POST})
	public Result doOpenSend(@RequestBody SysJointLogger sysJointLogger) {
		try{
			sysJointLoggerService.doOpenSend(sysJointLogger);
			return new SuccResult("对接成功");
		}catch (ServiceException ex){
			return new FailResult(""+ex.getMessage());
		}catch (Exception e){
			return new FailResult("对接失败!");
		}
	}


}
