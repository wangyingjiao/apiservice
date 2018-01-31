/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.result.OpenFailResult;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.Base64Decoder;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.MD5Util;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.Servlets;
import com.thinkgem.jeesite.modules.sys.utils.OpenLogUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 日志拦截器
 *
 * @author ThinkGem
 * @version 2014-8-19
 */
public class OpenLogInterceptor extends BaseService implements HandlerInterceptor {

    private static final ThreadLocal<Long> startTimeThreadLocal =
            new NamedThreadLocal<Long>("ThreadLocal StartTime");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        //HashMap<Object, Object> map = new HashMap<>();

        if (null != request.getContentType()
                && request.getMethod().toLowerCase().equals("post")) {
            String json = "";
            logger.info("==============JSON请求===========>"+request.getRequestURI());
            Map parameterMap = request.getParameterMap();
            JSON.toJSONString(parameterMap);
            String line;
            BufferedReader buf = request.getReader();
            while ((line = buf.readLine()) != null) {
                json += line;
            }
            if(StringUtils.isBlank(json)){
                json = request.getHeader("data");
            }
            if (StringUtils.isNotBlank(json)) {
                request.setAttribute("openJson",json);
                String decode = Base64Decoder.decode(json);//解密值
                logger.info(decode);
            }
        } else {
            logger.info("==============普通请求===========");
            Map requestParameterMap = request.getParameterMap();
            logger.info(JSON.toJSONString(requestParameterMap));
        }

        if (logger.isDebugEnabled()) {
            long beginTime = System.currentTimeMillis();//1、开始时间
            startTimeThreadLocal.set(beginTime);        //线程绑定变量（该数据只有当前请求的线程可见）
            logger.debug("开始计时: {}  URI: {}", new SimpleDateFormat("hh:mm:ss.SSS")
                    .format(beginTime), request.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            logger.info("ViewName: " + modelAndView.getViewName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 保存日志
        OpenLogUtils.saveLog(request, handler, ex, null);
    }

}
