package com.leil.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ShiroController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @PostMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("rememberMe: " + rememberMe);
        boolean rememberMeBool = false;
        if (!StringUtils.isEmpty(rememberMe) && StringUtils.equals(rememberMe, "true")) {
            rememberMeBool = true;
        }
        System.out.println("bool remember me: " + rememberMeBool);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(rememberMeBool);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write(e.getMessage());
            return;
        }
        System.out.println("isRemembered: " + subject.isRemembered());
        if (subject.isAuthenticated()) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write("login success!");
        }

    }


    @GetMapping("/unauthorized")
    @ResponseBody
    public String unauthorized() {
        return "你没有权限访问该页面";
    }


}
