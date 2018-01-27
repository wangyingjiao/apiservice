
package com.thinkgem.jeesite.modules.sys.interceptor;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.modules.service.service.skill.SerSkillItemService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * @ClassName: SessionExpiredInterceptor
 * @Description: TODO统一处理用户session过期后的Interceptor
 * @author WYR
 * @date 2018年1月26日 下午3:34:09
 *
 */
public class SessionExpiredInterceptor   implements HandlerInterceptor {
	@Autowired
	private SerSkillItemService systemService;
	private Log loger = LogFactory.getLog(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		loger.info("=================进入SessionExpiredInterceptor");
		HttpSession session = request.getSession(false);
		if (null==session) {
			Principal principal = UserUtils.getPrincipal();
			if (null == principal) {
				UserUtils.getSubject().logout();
				response.getWriter().print(JSON.toJSONString(new FailResult(0, "未登录或失败")));
				return false;
			}
		}

		User user = UserUtils.getUser();
		if (null != user) {
			User cache = UserUtils.getUserCache(user.getId());
			if (null == cache) {
				loger.info("=============session过期请重新登录");
				// UserUtils.clearCache();
				response.getWriter().print(JSON.toJSONString(new FailResult(2, "session过期请重新登录")));
				return false;
			}
		}
		return true;

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		/*
		 * request.setCharacterEncoding("UTF-8");
		 * response.setContentType("text/html;charset=utf-8"); String[]
		 * parameterValues = request.getParameterValues("username"); String
		 * parameterValues1 = request.getParameter("username"); Enumeration
		 * enumeration = request.getParameterNames(); Map map =
		 * request.getParameterMap();
		 */

		loger.info("SessionExpiredInterceptor======>postHandle处理！");

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		loger.info("SessionExpiredInterceptor=======>afterCompletion处理！");
	}


}