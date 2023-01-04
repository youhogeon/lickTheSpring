# 4. Annotation Context와 두 개의 Bean

# Annotation을 이용한 빈 설정

XML을 이용해 빈을 설정하는 것은 상당히 번거롭다.

Spring 3.2 버전부터 Java의 Annotation 문법을 사용해 빈 설정이 가능하도록 지원한다.

```java
package com.sample.spring;

t.ApplicationContext;
t.annotation.AnnotationConfigApplicationContext;
//t.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        //ApplicationContext context = new ClassPathXmlApplicationContext("services.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        (new Application(context)).run();
    }

}
```

```java
package com.sample.spring;

t.annotation.Bean;
t.annotation.Configuration;

ry.SampleRepository;
ry.SampleRepositoryImpl1;

@Configuration
public class Config {

    @Bean
    public SampleRepository sampleRepository() {
        return new SampleRepositoryImpl1();
    }

}
```

ApplicationContext 를 구현한 구현체는 *ClassPathXmlApplicationContext나* AnnotationConfigApplicationContext 외에도, GenericGroovyApplicationContext 등이 있다. 반드시 XML이나 어노테이션 형식을 사용해야 하는 것은 아니다.

# 두 개의 Bean 등록

이번에는 SampleRepositoryImpl2도 빈으로 등록해보자.

```java
package com.sample.spring;

t.annotation.Bean;
t.annotation.Configuration;

ry.SampleRepository;
ry.SampleRepositoryImpl1;
ry.SampleRepositoryImpl2;

@Configuration
public class Config {

    @Bean
    public SampleRepository secondaryRepository() {
        return new SampleRepositoryImpl1();
    }

    @Bean
    public SampleRepository primaryRepository() {
        return new SampleRepositoryImpl2();
    }

}
```

과연 잘 실행될까? 그렇지 않다.

```java
context.getBean(SampleRepository.class)
```

위와 같이 SampleRepository 타입의 Bean을 Spring context에게 요청하면, Spring context는 해당 타입의 Bean이 두 개가 있어서 당황할 것이다.

따라서 아래와 같이 NoUniqueBeanDefinitionException(RuntimeException)을 발생시킨다.

```java
Exception in thread "main"y.NoUniqueBeanDefinitionException: No qualifying bean of type 'com.sample.spring.repository.SampleRepository' available:t found 2: sampleRepository,sampleRepository2
```

이런 경우 특정 타입으로 Bean을 요청하는 것이 아니라, Bean 이름을 통해 Bean을 받아올 수 있다.

Config class의 메서드 명이 기본 Bean 이름으로 지정된다.

XML 설정에서도 비슷하게 id attribute의 값이 기본 Bean 이름이 된다.

```java
SampleRepository sampleRepository = (SampleRepository) context.getBean("primaryRepository");
```

Bean 이름으로 Bean을 가져오기 위해서는 getBean 메서드에 Bean 이름을 지정해주면 된다.

다만, 이런 경우 getBean은 Object 타입으로 반환해주기 때문에, 타입 캐스팅이 필요하다.

타입 캐스팅이 번거로운 경우 아래와 같이 사용할 수도 있다.

Java의 제너릭 문법을 통해 두 번째 인자로 지정한 타입으로 반환된다.

스프링 공식 문서에서는 이 방법을 권장한다.

([https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-client](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-client))

```java
SampleRepository sampleRepository = context.getBean("primaryRepository", SampleRepository.class);
```

# @Primary

Bean 이름에 의존하는 코드를 원치 않는다면, 설정 파일에서 @Primary 애노테이션을 사용할 수 있다.

```java
@Bean
@Primary
public SampleRepository primaryRepository() {
    return new SampleRepositoryImpl2();
}
```

Spring context는 @Primary 애노테이션이 붙은 빈을 우선적으로 반환한다.