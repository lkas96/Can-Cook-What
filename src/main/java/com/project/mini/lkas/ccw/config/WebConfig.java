package com.project.mini.lkas.ccw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.mini.lkas.ccw.component.LoginComponent;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginComponent loginInterceptor;

    //Forces login view only
    //anything with /home/**/
    //including /home     /home/cart    /home/recipes/recipe-id
    //Will trigger back to login page no permission to view
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/home/**", "/basket/**", "/recipe/**", "/log/**");
    }
}