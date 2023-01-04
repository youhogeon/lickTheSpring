# 15. Spring AOP(Aspect Oriented Programming)

# AOP란?

관점 지향 프로그래밍은 메서드의 공통 로직(로깅 등)을 비즈니스 로직하고자 하는 프로그래밍 기법이다.

코드에 불필요한 중복을 줄이고 비즈니스 로직에만 집중할 수 있다.

![Untitled](15%20Spring%20AOP(Aspect%20Oriented%20Programming)%2FUntitled.png)

Spring에서는 AOP를 지원하기 위해 JDK 동적 프록시(java.lang.reflect.proxy)를 이용해 프록시 객체를 만든다.

그러나 원본 클래스가 인터페이스를 구현하지 않는 경우에는 예외적으로 CGLIB을 이용해 프록시를 만든다. (설정 파일이 CGLIB 프록시가 되는 것 처럼)

# AspectJ?

AspectJ는 Java에서 AOP를 지원하기 위해 만들어진 오픈 소스 프로젝트이다.

자바 기반 문법을 사용하지만, 순수 자바와는 다소 차이가 있고, “.aj” 의 확장자를 가진다.

```java
package test;

public aspect SampleAspectClass {
    
    public String SampleJavaClass.name = "SampleJavaClass에 name이라는 변수가 생김";
    
    public void SampleJavaClass.print(){
        System.out.println("SampleJavaClass에 print라는 메서드가 생김");
    }

		pointcut pc_say(SampleJavaClass p): target(p) && call(void SampleJavaClass.print*()); 
    before(SampleJavaClass p) : pc_print(p) {
        System.out.println("before print");
    }   
    after(SampleJavaClass p) : pc_print(p) {
        System.out.println("after print");
    }
}
```

위와 같은 aspect를 생성하고 AspectJ 컴파일러를 이용해 컴파일하면 자바 코드로 변환된다.

원래 자바 코드였던 SampleJavaClass.java도 함께 변환되어, SampleJavaClass의 포인트컷에 SampleAspectClass 에 구현한 advice가 적용된다.

JDK 동적 프록시를 사용하는 Spring AOP와 달리 컴파일 단계에서 바이트코드 자체를 바꾸어버리기 때문에, Spring AOP보다 훨씬 빠르다.

따라서 목적에 따라 빠른 속도가 필요하면 AspectJ, 스프링과의 통합성이 중요하면 Spring AOP를 사용하면 된다.

> AOP에 대한 Spring AOP의 접근 방식은 대부분의 다른 AOP 프레임워크와 다릅니다. Spring AOP의 목표는 가장 완전한 AOP 구현을 제공하는 것이 아닙니다(Spring AOP도 상당히 유능하지만). Spring AOP의 목표는 AOP 구현과 Spring IoC 간의 긴밀한 통합을 제공하여 엔터프라이즈 애플리케이션의 일반적인 문제를 해결하는 데 도움을 주는 것입니다.
> 

자세한 설명은 이 글을 추천한다

[https%3A%2F%2Fnhj12311.tistory.com%2F470](https://nhj12311.tistory.com/470)

# Spring AOP 어노테이션 vs XML

Spring에서는 AspectJ 컴파일러를 사용하는 것은 아니지만 AspectJ의 일부 어노테이션 등을 재사용한다.

AspectJ의 어노테이션을 이용해 어노테이션 방식으로 Spring AOP를 사용할 수 있다.

따라서 AspectJ의 aspectjweaver 의존성을 추가해 주어야 한다.

[https%3A%2F%2Fmvnrepository.com%2Fartifact%2Forg.aspectj%2Faspectjweaver%2F1.9.19](https://mvnrepository.com/artifact/org.aspectj/aspectjweaver/1.9.19)

의존성을 추가한다고 해서 AspectJ의 AOP를 사용하는 것은 아니다.

주의할 점은, AspectJ는 해당 어노테이션들을 런타임에서만 사용하기 때문에 의존성 scope를 runtime으로 설정하면 안된다. Maven Repository에서 만들어준 의존성 코드는 scope가 runtime으로 되어있기 때문에 삭제 후 pom.xml에 넣어야 한다.

(참고) AspectJ는 컴파일 시점에(개발자의 코드에) 어노테이션을 사용할 일이 없다. 해당 어노테이션은 .aj 파일을 AspectJ 컴파일러가 컴파일 하면서 만든 바이트코드에만 사용한다.

```java
<!-- pom.xml에 AspectJ weaver 의존성 추가 -->

<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.19</version>
    <!-- <scope>runtime</scope> 삭제해야 함! -->
</dependency>
```

이 방식이 마음에 들지 않는다면, XML 기반으로 AOP 설정을 할 수 있다.

XML설정과 관련된 자세한 설명은 이 글을 추천한다.

[https%3A%2F%2Fprivate.tistory.com%2F43](https://private.tistory.com/43)

# AOP의 용어들

AOP 자체가 어려운 개념은 아니지만, 개발 시 알아야 할 용어가 많아 어렵게 느껴질 수 있다.

- Aspect : 여러 관심사의 집합. (Advice + Pointcut)
- Advice : AOP로 처리할 로직 (비즈니스 로직이 아닌 로직)
- Join point : Advice가 적용될 수 있는 위치
- Pointcut : Join point 중 Advice를 적용할 위치(특정 조건을 만족하는 하나 또는 그 이상의 join point들)
- Introduction : target object에 추가 메서드 또는 필드를 선언
- Target object : Advice가 적용될 (비즈니스 로직이 있는)객체. Spring AOP에 의해 Aop Proxy가 됨
- AOP proxy : AOP 프레임워크에 의해 생성된 객체. Spring AOP에서는 JDK 동적 프록시 또는 CGLIB 프록시임.
- Weaving : Pointcut에 Advice를 삽입하는 과정

Advice의 종류

- Before advice : join point 이전에 실행. (예외를 던지지 않는 한 실행 흐름을 막을 수는 없음)
- After returning advice : join point가 정상적으로 완료되면 실행
- After throwing advice : join point가 예외를 던지면 실행
- After (finally) advice : join point의 정상 종료 여부 상관 없이 실행
- Around advice : join point를 둘러싸며 실행(범용적임)

# Aspect 선언

클래스를 Aspect 클래스로 지정하기 위해서는 @Aspect 어노테이션(org.aspectj.lang.annotation.Aspect)을 사용하면 된다.

단, Aspect 클래스는 스프링 빈으로 등록되어 있어야 하며, 그래야 스프링이 Bean 객체를 생성하며 AOP를 적용할 수 있다.

```java
@Aspect
@Component
public class SampleAspect { ... }
```

# Pointcut 선언

Spring AOP는 AspectJ 포인트컷 표현식과 @Pointcut 어노테이션(org.aspectj.lang.annotation.Pointcut)을 이용해 포인트컷을 설정한다.

더불어, Spring AOP는 메서드 실행 join point만 지원한다.

```java
@Aspect
@Component
public class SampleAspect {

	@Pointcut("within(com.sample.spring.*)")
	private void pointcutMethod() { /* 빈 메서드 */ }

}
```

이렇게 @Pointcut이 지정된 메서드를 pointcut signature 이라고 하고, 반드시 반환 유형이 void이어야 한다.

pointcut signature는 추후 Advice에 Pointcut을 지정할 때

```java
@Before("pointcutMethod()")
@After("pointcutMethod()")
@Around("pointcutMethod()")
```

와 같이 사용할 수 있다.

```java
@Before("within(com.sample.spring.*)")
@After("within(com.sample.spring.*)")
@Around("within(com.sample.spring.*)")
```

Pointcut signature가 없다면 위와 같이 직접 포인트컷 표현식을 통해 포인트컷을 설정할 수 있다.

AspectJ 포인트컷 지정자는 다음과 같다.

- within : 특정 타입 클래스의 모든 메서드 조인포인트에 매칭
- execution (기본값)
    
    **execution([접근제어자] 리턴타입 [클래스명(패키지포함)].메서드명(파라미터))** 형식으로 지정
    
    *는 모든 값을 의미, ..는 0개 이상을 의미
    
    접근제어자와 클래스명은 선택사항
    
    execution(public String com.sample..get*(..)) → com.sample 하위 패키지 클래스 중 get으로 시작하며 String을 리턴하고 매개변수가 0개 이상인 public 메서드
    
    execution(* com.sample.repository.*.*(*, *)) → com.sample.repository의 모든 클래스의 모든 메서드(모든 반환타입) 중 매개변수가 두 개인 메서드
    
    execution(* print*(..)) → print로 시작하는 모든 메서드
    
- this : Spring AOP 프록시가 주어진 유형의 인터페이스 타입일 때 모든 메서드
- target : 프록시 원본 객체가 주어진 유형의 인터페이스 타입일 때 모든 메서드
- args : 인자가 주어진 유형의 타입인 메서드
- @target : 실행 객체 클래스에 주어진 타입의 어노테이션이 있는 경우 모든 메서드
- @within : 실행 객체 클래스에 주어진 타입의 어노테이션이 있는 경우 해당 객체의 메서드(부모 메서드 X)
- @annotation : 주어진 어노테이션을 가지고 있는 메서드
- @args : 인자가 주어진 어노테이션을 가지고 있는 메서드

추가로 Spring AOP에서는 bean 포인트컷 지정자를 지원한다.

- bean(tradeService) : tradeServcie 이름을 갖는 빈
- bean(*Service) : Service로 끝나는 이름의 빈

여러 포인트컷 표현식을 && 또는 || 연산자로 함께 사용할 수 있다.

# Advice 선언

```java
@Before("execution(* com.xyz.myapp.dao.*.*(..))")
public void doAccessCheck() { ... }

@AfterReturning("com.xyz.myapp.CommonPointcuts.dataAccessOperation()") //pointcut signature
public void doAccessCheck() { ... }

@AfterReturning(
    pointcut="com.xyz.myapp.CommonPointcuts.dataAccessOperation()",
    returning="retVal")
public void doAccessCheck(Object retVal) { ... }

@AfterThrowing("com.xyz.myapp.CommonPointcuts.dataAccessOperation()")
public void doAccessCheck() { ... }

@AfterThrowing(
	  pointcut="com.xyz.myapp.CommonPointcuts.dataAccessOperation()",
	  throwing="ex")
public void doRecoveryActions(DataAccessException ex) { ... }

@After("com.xyz.myapp.CommonPointcuts.dataAccessOperation()")
public void doReleaseLock() { ... }

@Around("com.xyz.myapp.CommonPointcuts.businessService()")
public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
    // start stopwatch
    Object retVal = pjp.proceed();
    // stop stopwatch
    return retVal;
}
```

# 사용 사례

```java
@Aspect
public class ConcurrentOperationExecutor implements Ordered {

    private static final int DEFAULT_MAX_RETRIES = 2;

    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int order = 1;

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Around("com.xyz.myapp.CommonPointcuts.businessService()")
    public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
        int numAttempts = 0;
        PessimisticLockingFailureException lockFailureException;
        do {
            numAttempts++;
            try {
                return pjp.proceed();
            }
            catch(PessimisticLockingFailureException ex) {
                lockFailureException = ex;
            }
        } while(numAttempts <= this.maxRetries);
        throw lockFailureException;
    }
}
```

PessimisticLockingFailureException 이 DEFAULT_MAX_RETRIES 번 발생할 때 까지 retry를 하는 Advice

```java
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
```