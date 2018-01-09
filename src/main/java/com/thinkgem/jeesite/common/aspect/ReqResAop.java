package com.thinkgem.jeesite.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
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
