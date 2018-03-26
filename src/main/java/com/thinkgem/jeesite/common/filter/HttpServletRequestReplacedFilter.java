package com.thinkgem.jeesite.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
//网上找的方法 重复调用request.getReader())及request.getInputStream()
public class HttpServletRequestReplacedFilter implements Filter {

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            // System.out.println(httpServletRequest.getContentType());
            // System.out.println(httpServletRequest.getContextPath());
            // System.out.println(httpServletRequest.getRemoteHost());
            // System.out.println(httpServletRequest.getMethod());
            if ("POST".equals(httpServletRequest.getMethod().toUpperCase())
                    ) {
                //&& httpServletRequest.getContentType().equalsIgnoreCase(
                // "application/json")
                requestWrapper = new BodyReaderHttpServletRequestWrapper(
                        (HttpServletRequest) request);
            }
        }

        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

}
