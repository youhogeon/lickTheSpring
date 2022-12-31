package com.lickthespring.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

//@WebFilter("/*")
//Interceptor 테스트를 위해 주석처리
public class CharsetFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {        
        request.setCharacterEncoding("UTF-8");

        System.out.println("CharsetFilter.doFilter()");

        chain.doFilter(request, response);
    }
    
}
