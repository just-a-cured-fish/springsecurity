package com.yxh.springsecurity.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component("demoAuthenticationSuccessHandler")
public class DemoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(DemoAuthenticationSuccessHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 处理登录成功的请求
     * @author hdd
     * @date 2018/12/10 0010 10:16
     * @param httpServletRequest, httpServletResponse, authentication—封装登录信息的]
     * @return void
     */
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)
            throws IOException, ServletException {
        logger.info("登录成功");
        String username=httpServletRequest.getParameter("username");
        logger.info("当前登录用户"+username);
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("username",username);
        HttpSession session2 = httpServletRequest.getSession();
        httpServletResponse.sendRedirect("/demo.html");
        //
//        httpServletResponse.setContentType("application/json;charset=UTF-8");
//        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(authentication));


    }
}
