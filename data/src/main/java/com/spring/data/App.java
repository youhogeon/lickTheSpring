package com.spring.data;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.spring.data.repository.MemberRepository;

public class App {

    public static void main(String[] args) throws SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberRepository memberRepository = context.getBean(MemberRepository.class);
        System.out.println(memberRepository.findNameById(2L));
    }

}