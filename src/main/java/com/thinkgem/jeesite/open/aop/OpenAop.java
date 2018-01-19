package com.thinkgem.jeesite.open.aop;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.utils.AESCrypt;
import com.thinkgem.jeesite.common.utils.Base64Decoder;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.common.web.Servlets;
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

    @Pointcut("execution(* com.thinkgem.jeesite.open.web..*.*(..))")
    public void point() {
    }

    @Before("point()")
    public void before(JoinPoint jp) throws Exception {
        logger.info("==> app before ");

    }

    @Around("point()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        logger.info("==> 环绕处理");
        HttpServletRequest request = Servlets.getRequest();

        String md5Content = request.getHeader("HTTP_MD5");
        BufferedReader reader = request.getReader();
        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }

        try {
            String text = sb.toString();//密文数据
            String body = MD5Util.getStringMD5(text+Global.getConfig("openEncryptPassword_gasq"));//MD5 加密
            if(md5Content.equals(body)){
                String decode = Base64Decoder.decode(text);//解密值

                //获取连接点方法运行时的入参列表 数组
                Object[] args = jp.getArgs();

                if(args != null && args.length > 1) {
                    Object o = JSON.parseObject(decode, args[0].getClass());
                    args[0] = o;
                }
                return jp.proceed(args);
            }else{
                return new FailResult<>("加密验证失败");
            }
        } catch (Exception e) {
            logger.info("请求参数类型异常");
        }
        return new FailResult<>("系统异常");
    }

    @AfterReturning("point()")
    public void afterReturning(JoinPoint jp) throws Exception {
    }

    @AfterThrowing(value = "point()", throwing = "ex")
    public void doExceptionActions(JoinPoint jp, Throwable ex) {
        logger.error("APP拦截异常！", ex);
    }

}
