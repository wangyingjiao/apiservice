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
		String responseSendType="";
		if(null!= request.getAttribute("openJson")){
			requestContent = request.getAttribute("openJson").toString();
		}
		if(null!= request.getAttribute("openResponseJson")){
			responseContent = request.getAttribute("openResponseJson").toString();
		}
		if(null!= request.getAttribute("openResponseCode")){
			responseCode = request.getAttribute("openResponseCode").toString();
		}

		/*if(null!= request.getAttribute("openResponseSendType")){
			responseSendType = request.getAttribute("openResponseSendType").toString();
		}*/

		log.setSendType("gasq");
		log.setSource("gasq");
		log.setIsSuccess("1".equals(responseCode) ? SysJointLog.IS_SUCCESS_YES : SysJointLog.IS_SUCCESS_NO);
		log.setRequestContent(requestContent);
		log.setResponseContent(responseContent);
		// 异步保存日志
        System.out.println("-- OpenLogUtils---saveLog---" + log.toString());
		new SaveLogThread(log, handler, ex).start();
	}


	public static void saveSendLog(SysJointLog log){
		// 异步保存日志
        System.out.println("-- OpenLogUtils---saveSendLog---" + log.toString());
		new SaveOwnLogThread(log, null, null).start();
	}

	public static void updateSendLog(SysJointLog log) {
		// 异步保存日志
        System.out.println("-- OpenLogUtils---updateSendLog---" + log.toString());
		new UpdateLogThread(log, null, null).start();
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
			// 保存日志信息
			log.preInsert();
            System.out.println("-- OpenLogUtils---SaveLogThread---run---" + log.toString());
			sysJointLogDao.insert(log);
		}
	}

	public static class SaveOwnLogThread extends Thread{
		private SysJointLog log;
		private Object handler;
		private Exception ex;
		public SaveOwnLogThread(SysJointLog log, Object handler, Exception ex){
			super(SaveOwnLogThread.class.getSimpleName());
			this.log = log;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			// 保存日志信息
			log.preInsert();
            System.out.println("-- OpenLogUtils---SaveOwnLogThread---run---" + log.toString());
			sysJointLogDao.insert(log);
		}
	}

	public static class UpdateLogThread extends Thread{
		private SysJointLog log;
		private Object handler;
		private Exception ex;
		public UpdateLogThread(SysJointLog log, Object handler, Exception ex){
			super(UpdateLogThread.class.getSimpleName());
			this.log = log;
			this.handler = handler;
			this.ex = ex;
		}
		@Override
		public void run() {
			// 保存日志信息
			log.preUpdate();
            System.out.println("-- OpenLogUtils---UpdateLogThread---run---" + log.toString());
			sysJointLogDao.update(log);
		}
	}

}
