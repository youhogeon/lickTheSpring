package com.sample.spring.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class SampleRepositoryImpl2 implements SampleRepository {

    public int getLastUserId() {
        return 200;
    }

}
