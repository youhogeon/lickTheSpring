package com.sample.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sample.spring.repository.SampleRepository;


@Component
public class Application {

    private final SampleRepository sampleRepository;

    @Value("${group.key}") //SpEL 이용해 application.properties의 값을 가져옴
    private String test;

    public Application(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(test);
        System.out.println(sampleRepository.getLastUserId());
    }

}
