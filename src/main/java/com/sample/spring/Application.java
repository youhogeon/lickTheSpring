package com.sample.spring;

import org.springframework.stereotype.Component;

import com.sample.spring.repository.SampleRepository;

@Component
public class Application {

    SampleRepository sampleRepository;

    public Application(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}

