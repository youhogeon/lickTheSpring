package com.sample.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.sample.spring.repository.SampleRepository;
import com.sample.spring.repository.SampleRepositoryImpl1;
import com.sample.spring.repository.SampleRepositoryImpl2;

@Configuration
public class Config {

    @Bean
    public Application application() {
        return new Application(primaryRepository());
    }

    @Bean
    public SampleRepository secondaryRepository() {
        return new SampleRepositoryImpl1();
    }

    @Bean
    @Primary
    public SampleRepository primaryRepository() {
        return new SampleRepositoryImpl2();
    }

}