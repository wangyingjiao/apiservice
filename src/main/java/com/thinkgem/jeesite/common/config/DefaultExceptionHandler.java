package com.thinkgem.jeesite.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.thinkgem.jeesite.common.result.FailResult;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author x
 */
@Component
public class DefaultExceptionHandler implements HandlerExceptionResolver {
    Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {
        ModelAndView mv = new ModelAndView();
         //使用FastJson提供的FastJsonJsonView视图返回，不需要捕获异常
        FastJsonJsonView view = new FastJsonJsonView();
        FailResult<String> failResult = new FailResult<>(ex.getClass()+":"+ex.getMessage());
        JSONObject viewMap = JSONObject.parseObject(JSON.toJSONString(failResult));
        view.setAttributesMap(viewMap);
        mv.setView(view);
        logger.error("error",ex);
        return mv;
    }
}