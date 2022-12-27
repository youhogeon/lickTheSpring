package com.sample.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.sample.spring.repository.SampleRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class Application implements InitializingBean, DisposableBean {

    private final SampleRepository sampleRepository;

    public Application(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public SampleRepository getSampleRepository() {
        return sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("@PostConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("@PreDestroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy");
    }

}
