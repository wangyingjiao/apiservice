package com.thinkgem.jeesite.open.interceptor;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.result.FailResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token 拦截器
 *
 * @author x
 */

public class OpenTokenInterceptor implements HandlerInterceptor {

    private Log loger = LogFactory.getLog(getClass());

    private final String TOKEN = "TOKEN";

    @Autowired
    private OpenTokenManager tokenManager;

    /**
     * 检查token和生成token
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        loger.info("拦截器处理==>  进入app拦截处理");
        String header = request.getHeader(TOKEN);
        loger.debug("token 值为 ==>" + header);
        OpenToken token = tokenManager.verifyToken(new OpenToken(header));
        if (null == token) {
            response.getWriter().print(JSON.toJSONString(new FailResult<>("token 无效或 已经过期，请重新登录")));
            response.getWriter().flush();
            loger.info("token 无效或 已经过期，请重新登录" + header);
            return false;
        } else {
            request.setAttribute("token", token);
            loger.debug("==> token 正常,刷新token 时间 "+token.toString());
            tokenManager.updateToken(token);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        loger.info("==>后期处理！");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        loger.info("==>异常处理！");
    }


}
