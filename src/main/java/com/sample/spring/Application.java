package com.sample.spring;

import com.sample.spring.repository.SampleRepository;

public class Application {

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

}
