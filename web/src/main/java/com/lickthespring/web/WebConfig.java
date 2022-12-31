package com.lickthespring.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lickthespring.web.filter.CharsetInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final CharsetInterceptor charsetInterceptor;

    public WebConfig(CharsetInterceptor charsetInterceptor) {
        this.charsetInterceptor = charsetInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(charsetInterceptor);
    }

}
