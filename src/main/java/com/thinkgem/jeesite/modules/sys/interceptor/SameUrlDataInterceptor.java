package com.thinkgem.jeesite.modules.sys.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 一个用户 相同url 同时提交 相同数据 验证
 * 主要通过 session中保存到的url 和 请求参数。如果和上次相同，则是重复提交表单
 *
 * @author Administrator
 */
public class SameUrlDataInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            SameUrlData annotation = method.getAnnotation(SameUrlData.class);
            if (annotation != null) {
                //如果重复相同数据
                if (repeatDataValidator(request)) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    /**
     * 验证同一个url数据是否相同提交  ,相同返回true
     *
     * @param httpServletRequest
     * @return
     */
    public boolean repeatDataValidator(HttpServletRequest httpServletRequest) {
        Map body = new HashMap();
        if (httpServletRequest.getContentType().indexOf(MediaType.APPLICATION_JSON_VALUE) != -1) {
            body = getRequestBody(httpServletRequest);
        } else {
            body = httpServletRequest.getParameterMap();
        }
        String params = JsonMapper.toJsonString(body);

        String url = httpServletRequest.getRequestURI();
        Map<String, String> map = new HashMap<String, String>();
        map.put(url, params);
        String nowUrlParams = map.toString();

        Object preUrlParams = httpServletRequest.getSession().getAttribute("repeatData");
        //如果上一个数据为null,表示还没有访问页面
        if (preUrlParams == null) {
            httpServletRequest.getSession().setAttribute("repeatData", nowUrlParams);
            return false;
        } else {
            //否则，已经访问过页面
            //如果上次url+数据和本次url+数据相同，则表示城府添加数据
            if (preUrlParams.toString().equals(nowUrlParams)) {

                return true;
            } else//如果上次 url+数据 和本次url加数据不同，则不是重复提交
            {
                httpServletRequest.getSession().setAttribute("repeatData", nowUrlParams);
                return false;
            }

        }
    }

    private Map getRequestBody(HttpServletRequest request) {
        try {

            BufferedReader reader = request.getReader();
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            JSONObject jsonObject = JSON.parseObject(sb.toString());
            TreeMap<String, Object> treeMap = new TreeMap<>();
            Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                treeMap.put(entry.getKey(), entry.getValue());
            }
            return treeMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TreeMap();
    }

}  