package com.thinkgem.jeesite.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thoughtworks.xstream.mapper.Mapper.Null;

import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author x
 */
@Aspect
@EnableAspectJAutoProxy
public class ReqResAop {
	Log logger = LogFactory.getLog(getClass());

	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void point() {
		logger.info("-----------------------------------------");
	}

	// com.thinkgem.jeesite.modules.service.web.sort
	@Before("execution(* com.thinkgem..web..*.*(..))")
	public void before(JoinPoint jp) throws Exception {
		Object object[] = jp.getArgs();
		System.out.println(object[0]);
		for (int i = 0; i < jp.getArgs().length; i++) {
			System.out.println("ReqResAop类拦截器处理请求参数===========" + jp.getArgs()[i]);
			if (jp.getArgs()[i] != null) {
				String string = jp.getArgs()[i].toString();
				logger.info("转换前的参数=============" + string);
				String decode = URLDecoder.decode(string, "UTF-8");
				logger.info("转换后的参数=============" + decode);
			}
		}
		logger.info(" class[" + jp.getTarget().getClass().getName() + "] - " + "method[" + jp.getSignature().getName()
				+ "]\r传入参数：" + object[0]);
	}

	@Around("point()")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		Object proceed = jp.proceed();
		logger.info("返回值：" + JsonMapper.toJsonString(proceed));
		return proceed;
	}

	@AfterReturning("point()")
	public void afterReturning(JoinPoint jp) throws Exception {
	}

	@AfterThrowing(value = "point()", throwing = "ex")
	public void doExceptionActions(JoinPoint jp, Throwable ex) {

	}
}
