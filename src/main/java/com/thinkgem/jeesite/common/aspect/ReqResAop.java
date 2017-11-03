package com.thinkgem.jeesite.common.aspect;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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

        logger.info(jp.getSourceLocation());
        logger.info(jp.getTarget());
        logger.info(jp.getKind());
        logger.info(jp.getStaticPart());
        logger.info(jp.getArgs());
    }

    @Around("point()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        Object proceed = jp.proceed();
        logger.info("返回值："+ JSON.toJSONString(proceed));
        return proceed;
    }

    @AfterReturning("point()")
    public void afterReturning(JoinPoint jp) throws Exception {
    }

    @AfterThrowing(value = "point()", throwing = "ex")
    public void doExceptionActions(JoinPoint jp, Throwable ex) {

    }
}
