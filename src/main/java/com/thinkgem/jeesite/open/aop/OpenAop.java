package com.thinkgem.jeesite.open.aop;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.utils.AESCrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

/**
 * @author x
 */
@Aspect
@EnableAspectJAutoProxy
public class OpenAop {

    Log logger = LogFactory.getLog(getClass());

    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(* com.thinkgem.jeesite.app.login..*.*(..))")
    public void point() {
    }

    @Before("point()")
    public void before(JoinPoint jp) throws Exception {
        logger.info("==> app before ");

    }

    @Around("point()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        logger.info("==>app aop环绕处理");
        String token = request.getHeader("token");
        String md5 = request.getHeader("md5");
        BufferedReader reader = request.getReader();
        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        AESCrypt crypt = new AESCrypt(Global.getConfig("appAesPassword"));
        try {
            String text = sb.toString();
            logger.info("密文数据："+text);
            String decrypt = crypt.decrypt(text);
            logger.debug("解密值为："+decrypt);
            //获取连接点方法运行时的入参列表 数组
            Object[] args = jp.getArgs();
            System.out.println(args);
            if(args != null && args.length > 1) {
                Object o = JSON.parseObject(decrypt, args[0].getClass());
                args[0] = o;
            }
            return jp.proceed(args);
        } catch (Exception e) {
            logger.info("解密异常！！");
        }
        return new FailResult<>("未知错误！");
    }

    @AfterReturning("point()")
    public void afterReturning(JoinPoint jp) throws Exception {
    }

    @AfterThrowing(value = "point()", throwing = "ex")
    public void doExceptionActions(JoinPoint jp, Throwable ex) {
        logger.error("APP拦截异常！", ex);
    }

}
