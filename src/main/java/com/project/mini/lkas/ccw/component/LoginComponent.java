package com.project.mini.lkas.ccw.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginComponent implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object userSession = request.getSession().getAttribute("loggedInUser");

        if (userSession == null) {

            response.sendRedirect("/login");

            return false;
            
        } else {

            return true;

        }


    }
}
