package com.thinkgem.jeesite.common.filter;


import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author x
 */
public class DefaultServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (null != request.getContentType() &&
                request.getContentType().toLowerCase().indexOf(MediaType.APPLICATION_JSON_VALUE) != -1) {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest req = (HttpServletRequest) request;
                if (req.getMethod().toUpperCase().equals(RequestMethod.POST + "")) {
                    requestWrapper = new DefaultHttpServletRequestWrapper(req);
                }
            }
        }
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {
    }
}
