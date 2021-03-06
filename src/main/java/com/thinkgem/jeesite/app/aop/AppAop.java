package com.thinkgem.jeesite.app.aop;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.redis.JedisConstant;
import com.thinkgem.jeesite.common.result.AppFailResult;
import com.thinkgem.jeesite.common.result.AppSuccResult;
import com.thinkgem.jeesite.common.utils.*;
import com.thinkgem.jeesite.modules.service.entity.appVersion.AppVersion;
import com.thinkgem.jeesite.modules.service.service.appVersion.AppVersionService;
import com.thinkgem.jeesite.modules.sys.entity.VersionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;


/**
 * @author x
 */
@Aspect
@EnableAspectJAutoProxy
public class AppAop {

    Log logger = LogFactory.getLog(getClass());

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AppVersionService appVersionService;

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
        //取build号比较redis中的build 一致就不管 不一致是否强更
        String header = request.getHeader("appBuild");
        //收到的build号
        Long receviceBuild = Long.valueOf(header);
        String cache = JedisUtils.get(JedisConstant.KEY_PREFIX + ":" + JedisConstant.CACHE_NEWEST_VERSION);
        AppVersion appVersion = JSON.parseObject(cache, AppVersion.class);
        // AppVersion appVersion = (AppVersion) CacheUtils.get(CACHE_NEWEST_VERSION);
        if (appVersion == null) {
            appVersion = appVersionService.getNewest();
        }
        String s1 = appVersion.getBuild().toString();
        boolean equals = header.equals(s1);
        //两个版本的build不同
        if (appVersion !=null && !equals){
            Long forceBuild1 = Long.valueOf(s1);
            String forcedUpdate = appVersion.getForcedUpdate();
            if ("yes".equals(forcedUpdate)){
                if (forceBuild1>receviceBuild) {
                    return new AppSuccResult(300, appVersion, "版本更新");
                }
            }else{
                //比较最新一版强更build与本版本的build
                VersionInfo forcedAppVersion = appVersion.getForcedAppVersion();
                if (forcedAppVersion != null) {
                    BigDecimal build1 = forcedAppVersion.getBuild();
                    String s = build1.toPlainString();
                    //强更版本的build号
                    Long forceBuild = Long.valueOf(s);
                    //如果强更版本的build大 将最新的版本变为强更版本返回给前端
                    if (forceBuild>receviceBuild){
                        appVersion.setForcedUpdate("yes");
                        return new AppSuccResult(300,appVersion,"版本更新");
                    }
                }
            }
        }
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
            String decode = Base64Decoder.decode(text);
            logger.debug("解密值为："+decode);
            //获取连接点方法运行时的入参列表 数组
            Object[] args = jp.getArgs();
            if(args != null && args.length > 1) {
                Object o = JSON.parseObject(decode, args[0].getClass());
                args[0] = o;
            }
            return jp.proceed(args);
        } catch (Exception e) {
            logger.info("解密异常！！");
        }
        return new AppFailResult<>(-1,null,"未知错误！");
    }

    @AfterReturning("point()")
    public void afterReturning(JoinPoint jp) throws Exception {
    }

    @AfterThrowing(value = "point()", throwing = "ex")
    public void doExceptionActions(JoinPoint jp, Throwable ex) {
        logger.error("APP拦截异常！", ex);
    }

}
