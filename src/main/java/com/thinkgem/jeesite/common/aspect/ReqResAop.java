package com.thinkgem.jeesite.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thoughtworks.xstream.mapper.Mapper.Null;


import java.net.URLDecoder;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.net.URLDecoder;

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

	
	  @Before("point()")
	  public void before(JoinPoint jp) throws Exception {
	  
	 }
	 
	/*@Before("execution(* com.thinkgem..web..*.*(..))")
	public void before(JoinPoint jp) throws Exception {
		Object object[] = jp.getArgs();
		if (object != null && object.length > 0) {
			System.out.println(object[0]);
			for (int i = 0; i < jp.getArgs().length; i++) {
				logger.info("ReqResAop类拦截器处理请求参数===========" + jp.getArgs()[i]);
				if (jp.getArgs()[i] != null) {
					String string = jp.getArgs()[i].toString();
					logger.info("转换前的参数=============" + string);
					if (StringUtils.isNotEmpty(string)) {
						jp.getArgs()[i] = URLDecoder.decode(string, "UTF-8");
						logger.info("转换后的参数=============" + jp.getArgs()[i]);
					}
				}
			}
			logger.info(" class[" + jp.getTarget().getClass().getName() + "] - " + "method["
					+ jp.getSignature().getName() + "]\r传入参数：" + object[0]);
		}
	}*/
	
	/*@Around("execution(* com.thinkgem..web..*.*(..))")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		Class<? extends Object[]> aClass = jp.getArgs().getClass();
		Object[] object = jp.getArgs();
		if (object != null && object.length > 0) {
			System.out.println(Arrays.toString(object));
			for (int i = 0; i < object.length; i++) {
				logger.info("ReqResAop类拦截器处理请求参数===========" + jp.getArgs()[i]);
				if (object[i] != null) {
					String string = object[i].toString();
					logger.info("转换前的参数=============" + string);
					if (StringUtils.isNotEmpty(string)) {
						object[i] = URLDecoder.decode(string, "UTF-8");
//						Object obj = object[0];
			            //obj= JSON.parseObject(jp.getArgs()[i].toString(),obj.getClass());
//			            object[0]=obj;
			            //logger.info("转换后内容：" + JSONObject.toJSONString(obj));
						logger.info("转换后的参数=============" + Arrays.toString(object));
					}
				}
			}
			logger.info(" class[" + jp.getTarget().getClass().getName() + "] - " + "method["
					+ jp.getSignature().getName() + "]\r传入参数：" + object[0]);
		}
	
		Object proceed = jp.proceed(object);
		logger.info("返回值：" + JsonMapper.toJsonString(proceed));
		return proceed;

	}*/
	@Around("point()")
	public Object around(ProceedingJoinPoint jp) throws Throwable {

		Object proceed = jp.proceed();
		logger.info("返回值：" + JsonMapper.toJsonString(proceed));
		return proceed;
	}
 
/*   @Before("execution(* com.thinkgem..web..*.*(..))")
   public void before(JoinPoint jp) throws Exception {
       for (int i = 0; i < jp.getArgs().length; i++) {
           System.out.println("ReqResAop类拦截器处理请求参数===========" + jp.getArgs()[i]);
           if (jp.getArgs()[i] != null) {
               String string = jp.getArgs()[i].toString();
               System.out.println("转换前的参数=============" + string);
               String decode = URLDecoder.decode(string, "UTF-8");
               System.out.println("转换后的参数=============" + decode);
           }
       }
   }*/


	

	@AfterReturning("point()")
	public void afterReturning(JoinPoint jp) throws Exception {
	}

	@AfterThrowing(value = "point()", throwing = "ex")
	public void doExceptionActions(JoinPoint jp, Throwable ex) {

	}
}
