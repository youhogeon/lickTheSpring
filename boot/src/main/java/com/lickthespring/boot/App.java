package com.lickthespring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
public class App 
{
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(App.class, args);

        for (String beanDefinitionName : run.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }

        System.out.println("There are " + run.getBeanDefinitionCount() + " beans in the application context");
    }

}
