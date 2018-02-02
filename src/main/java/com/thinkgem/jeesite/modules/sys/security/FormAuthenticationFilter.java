/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkgem.jeesite.common.result.FailResult;
import com.thinkgem.jeesite.common.result.Result;
import com.thinkgem.jeesite.common.result.SuccResult;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 表单验证（包含验证码）过滤类
 *
 * @author ThinkGem
 * @version 2014-5-19
 */
@Service
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

	public static final String DEFAULT_CAPTCHA_PARAM = "validateCode";
	public static final String DEFAULT_MOBILE_PARAM = "mobileLogin";
	public static final String DEFAULT_MESSAGE_PARAM = "message";

	private String captchaParam = DEFAULT_CAPTCHA_PARAM;
	private String mobileLoginParam = DEFAULT_MOBILE_PARAM;
	private String messageParam = DEFAULT_MESSAGE_PARAM;

	private Log logger = LogFactory.getLog(getClass());

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
		String json = new String();
		String temp;
		String username;
		String password;
		logger.info(request.getContentType());
		// 如果用户提交使用的json body post 方式登录
		if (request.getContentType().indexOf(FastJsonJsonView.DEFAULT_CONTENT_TYPE) != -1) {
			try {
				BufferedReader reader = request.getReader();
				while ((temp = reader.readLine()) != null) {
					json += temp;
				}
				JSONObject object = JSON.parseObject(json);
				username = object.getString(getUsernameParam());
				password = object.getString(getPasswordParam());
			} catch (IOException e) {
				return new UsernamePasswordToken();
			}
		} else {
			username = getUsername(request);
			password = getPassword(request);
		}

		if (password == null) {
			password = "";
		}
		boolean rememberMe = isRememberMe(request);
		String host = StringUtils.getRemoteAddr((HttpServletRequest) request);
		String captcha = getCaptcha(request);
		boolean mobile = isMobileLogin(request);
		return new UsernamePasswordToken(username, password.toCharArray(), rememberMe, host, captcha, mobile);
	}

	/**
	 * 获取登录用户名
	 */
	protected String getUsername(ServletRequest request, ServletResponse response) {
		String username = super.getUsername(request);
		if (StringUtils.isBlank(username)) {
			username = StringUtils.toString(request.getAttribute(getUsernameParam()), StringUtils.EMPTY);
		}
		return username;
	}

	/**
	 * 获取登录密码
	 */
	@Override
	protected String getPassword(ServletRequest request) {
		String password = super.getPassword(request);
		if (StringUtils.isBlank(password)) {
			password = StringUtils.toString(request.getAttribute(getPasswordParam()), StringUtils.EMPTY);
		}
		return password;
	}

	/**
	 * 获取记住我
	 */
	@Override
	protected boolean isRememberMe(ServletRequest request) {
		String isRememberMe = WebUtils.getCleanParam(request, getRememberMeParam());
		if (StringUtils.isBlank(isRememberMe)) {
			isRememberMe = StringUtils.toString(request.getAttribute(getRememberMeParam()), StringUtils.EMPTY);
		}
		return StringUtils.toBoolean(isRememberMe);
	}

	public String getCaptchaParam() {
		return captchaParam;
	}

	protected String getCaptcha(ServletRequest request) {
		return WebUtils.getCleanParam(request, getCaptchaParam());
	}

	public String getMobileLoginParam() {
		return mobileLoginParam;
	}

	protected boolean isMobileLogin(ServletRequest request) {
		return WebUtils.isTrue(request, getMobileLoginParam());
	}

	public String getMessageParam() {
		return messageParam;
	}

	/**
	 * 登录成功之后跳转URL
	 */
	@Override
	public String getSuccessUrl() {
		return super.getSuccessUrl();
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {

		Result<HashMap<String, Object>> result = new SuccResult<>();
		HashMap<String, Object> map = new HashMap<>();
		map.put("message", "登录成功！");
		map.put("JSESSIONID", WebUtils.toHttp(request).getSession().getId());
		ObjectMapper mapper = new ObjectMapper();
		String s = mapper.writeValueAsString(UserUtils.getUser());
		map.put("user", JSON.parseObject(s));
		result.setData(map);
		response.setContentType(FastJsonJsonView.DEFAULT_CONTENT_TYPE);
		response.getWriter().print(JSON.toJSONString(result));
		return false;
	}

	@Override
	protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
		// Principal p = UserUtils.getPrincipal();
		// if (p != null && !p.isMobileLogin()){
		WebUtils.issueRedirect(request, response, getSuccessUrl(), null, true);

		// }else{
		// super.issueSuccessRedirect(request, response);
		// }
	}

	/**
	 * 登录失败调用事件
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		String className = e.getClass().getName(), message = "";
		if (IncorrectCredentialsException.class.getName().equals(className)
				|| UnknownAccountException.class.getName().equals(className)) {
			message = "用户或密码错误, 请重试.";
		} else if (e.getMessage() != null && StringUtils.startsWith(e.getMessage(), "msg:")) {
			message = StringUtils.replace(e.getMessage(), "msg:", "");
		} else {
			message = "系统出现点问题，请稍后再试！";
			e.printStackTrace(); // 输出到控制台
		}
		request.setAttribute(getFailureKeyAttribute(), className);
		request.setAttribute(getMessageParam(), message);
		return true;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// return super.onAccessDenied(request, response);
		//HttpServletRequest httpRequest = (HttpServletRequest) request;
		//HttpServletResponse httpResponse = (HttpServletResponse) response;
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		/*
		 * Subject subject = getSubject(request, response);
		 * 
		 * if (subject.getPrincipal() == null) { saveRequest(request);
		 * response.setContentType(FastJsonJsonView.DEFAULT_CONTENT_TYPE);
		 * response.getWriter().print(JSON.toJSONString(new Result<String>(0,
		 * "您尚未登录或登录时间过长,请重新登录!"))); response.getWriter().flush();
		 * 
		 * } else {
		 * response.setContentType(FastJsonJsonView.DEFAULT_CONTENT_TYPE);
		 * response.getWriter().print(JSON.toJSON(new Result<String>(0,
		 * "您没有足够的权限执行该操作!"))); response.getWriter().flush(); } return false;
		 */

		/*
		 * HttpSession session = httpRequest.getSession(false); if (null ==
		 * session) { return true; } return false; }
		 */

		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Login submission detected.  Attempting to execute login.检测到登录提交。尝试执行登录。 ");
				}
				return executeLogin(request, response);
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Login page view.");
				}
				// allow them to see the login page ;)
				return true;
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Attempting to access a path which requires authentication.  Forwarding to the "
						+ "Authentication url [" + getLoginUrl() + "]");
			}

			User user = UserUtils.getUser();
			logger.info("=================FormAuthenticationFiltersession是否失效先查询user:"
					+ JSON.toJSONString(user.toString()));

			User cache = UserUtils.getUserCache(user.getId());
			if (null == cache) {
				logger.info("=============FormAuthenticationFilter说明session过期请重新登录");
				response.getWriter()
						.print(JSON.toJSONString(new FailResult(2, "session过期请重新登录")));
				return false;
			}
			//saveRequestAndRedirectToLogin(request, response);
			return false;
		}
	}
}
