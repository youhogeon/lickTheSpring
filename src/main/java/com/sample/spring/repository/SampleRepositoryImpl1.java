package com.sample.spring.repository;

import org.springframework.stereotype.Repository;

@Repository
public class SampleRepositoryImpl1 implements SampleRepository {

    public int getLastUserId() {
        return 100;
    }

}
