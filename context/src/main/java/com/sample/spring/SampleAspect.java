package com.sample.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SampleAspect {

    @Around("within(com.sample.spring..*)")
    public Object aopLogger(ProceedingJoinPoint jointPoint) throws Throwable {
        String signatureStr = jointPoint.getSignature().toShortString();
        System.out.println("before " + signatureStr);

        long beginTime = System.nanoTime();
        
        try {
            return jointPoint.proceed();
        } finally {
            System.out.println("after " + signatureStr);
            System.out.println(signatureStr + " : " + (System.nanoTime() - beginTime) + "ns");
        }
    }

    @Before("within(com.sample.spring.*)")
    public void beforeAdvice() {
        System.out.println("beforeAdvice()");
    }

    @After("within(com.sample.spring.*)")
    public void afterAdvice() {
        System.out.println("afterAdvice()");
    }

}
