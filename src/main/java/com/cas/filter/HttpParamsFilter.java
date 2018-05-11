package com.cas.filter;


import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class HttpParamsFilter implements Filter {

    public static String REQUESTED_URL =  "CasRequestedUrl";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String requestPath ="";// WebUtils.getRealPath();
        session.setAttribute(REQUESTED_URL,requestPath);
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
