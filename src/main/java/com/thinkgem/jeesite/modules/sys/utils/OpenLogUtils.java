/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.utils;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.result.OpenResult;
import com.thinkgem.jeesite.common.utils.Base64Decoder;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.dao.SysJointLogDao;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 字典工具类
 * @author ThinkGem
 * @version 2014-11-7
 */
public class OpenLogUtils {
	
	private static SysJointLogDao sysJointLogDao = SpringContextHolder.getBean(SysJointLogDao.class);

	/**
	 * 保存日志
	 */
	public static void saveLog(HttpServletRequest request,Object handler, Exception ex, String title){
		SysJointLog log = new SysJointLog();
		log.setUrl(request.getRequestURI());
		String requestContent = "";
		String responseContent = "";
		String responseCode = "";
		if(null!= request.getAttribute("openJson")){
			requestContent = request.getAttribute("openJson").toString();
		}
		if(null!= request.getAttribute("openResponseJson")){
			responseContent = request.getAttribute("openResponseJson").toString();
		}
		if(null!= request.getAttribute("openResponseCode")){
			responseCode = request.getAttribute("openResponseCode").toString();
		}

		log.setIsSuccess("1".equals(responseCode) ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
		log.setRequestContent(requestContent);
		log.setResponseContent(responseContent);
		// 异步保存日志
		new SaveLogThread(log, handler, ex).start();
	}


	public static void saveSendLog(SysJointLog log){

		// 异步保存日志
		new SaveLogThread(log, null, null).start();
	}

	/**
	 * 保存日志线程
	 */
	public static class SaveLogThread extends Thread{
		
		private SysJointLog log;
		private Object handler;
		private Exception ex;
		
		public SaveLogThread(SysJointLog log, Object handler, Exception ex){
			super(SaveLogThread.class.getSimpleName());
			this.log = log;
			this.handler = handler;
			this.ex = ex;
		}
		
		@Override
		public void run() {
			// 如果有异常，设置异常信息
			//log.setException(Exceptions.getStackTraceAsString(ex));
			/*// 如果无异常日志，则不保存信息
			if ( StringUtils.isBlank(log.getException())){
				return;
			}*/
			// 保存日志信息
			log.preInsert();
			sysJointLogDao.insert(log);
		}
	}

}
