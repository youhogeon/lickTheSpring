package com.lickthespring.web.error;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyExceptionHandler implements HandlerExceptionResolver {

    @Override
    @Nullable
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        //ex가 처리 가능한 예외일 경우 예외 처리 후 ModelAndView를 반환하는 코드
        //그렇지 않으면 null 반환

        //return new ModelAndView("/WEB-INF/views/error.jsp");
        return null;
    }
    
}
