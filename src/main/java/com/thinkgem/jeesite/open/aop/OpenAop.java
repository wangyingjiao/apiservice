package com.thinkgem.jeesite.open.aop;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.OpenFailResult;
import com.thinkgem.jeesite.common.utils.AESCrypt;
import com.thinkgem.jeesite.common.utils.Base64Decoder;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.Servlets;
import com.thinkgem.jeesite.modules.sys.entity.SysJointLog;
import com.thinkgem.jeesite.modules.sys.utils.OpenLogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

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

        String md5Content = request.getHeader("md5");
        if(null == md5Content){
            return new OpenFailResult("请求参数有误");
        }

        /*StringBuilder sb2 = new StringBuilder();
        BufferedReader in=new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
        String line = null;
        while ((line = in.readLine()) != null) {
            sb2.append(line);
        }*/

        try {
            //String text = sb2.toString();//密文数据
            String text = request.getAttribute("openJson").toString(); //sb2.toString();//密文数据

            /*if(StringUtils.isBlank(text)){
                text = request.getHeader("data");
                System.out.println(text);
            }*/

            String body = MD5Util.getStringMD5(text+Global.getConfig("openEncryptPassword_gasq"));//MD5 加密
            if(md5Content.equals(body)){
                String decode = Base64Decoder.decode(text);//解密值
                System.out.println("decode---"+decode);
                request.setAttribute("openJson",decode);
                //获取连接点方法运行时的入参列表 数组
                Object[] args = jp.getArgs();

                if(args != null && args.length > 1) {
                    Object o = JSON.parseObject(decode, args[0].getClass());
                    args[0] = o;
                }
                return jp.proceed(args);
            }else{
                return new OpenFailResult("加密验证失败");
            }
        } catch (Exception e) {
            logger.info("请求参数类型异常");
        }
        return new OpenFailResult("请求参数类型异常");
    }

    @AfterReturning("point()")
    public void afterReturning(JoinPoint jp) throws Exception {
    }

    @AfterThrowing(value = "point()", throwing = "ex")
    public void doExceptionActions(JoinPoint jp, Throwable ex) {
        logger.error("OPEN拦截异常！", ex);
    }

}
