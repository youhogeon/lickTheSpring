# 7. Singleton Pattern

# Java Singleton

자바에서 Singleton 패턴을 구현하려면 아래와 같은 귀찮은 코드들이 필요하다.

```java
public class Singleton {

    private final static Singleton instance = new Singleton();
    
    private Singleton() {
        // 객체 생성 방지하기 위해 private 생성자 사용
    }

    public static Singleton getInstance() {
        return instance;
    }

    public void logic() { ... }
}
```

```java
Singleton obj1 = Singleton.getInstance();
Singleton obj2 = Singleton.getInstance();

obj1 == obj2 //true
```

# Spring Context와 Singleton

Spring context는 기본적으로 모든 Bean이 Singleton임을 보장해준다.

(당연히 변경 가능하다)

```java
package com.sample.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sample.spring.repository.SampleRepository;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        SampleRepository sampleRepository1 = context.getBean(SampleRepository.class);
        SampleRepository sampleRepository2 = context.getBean(SampleRepository.class);

        System.out.println(sampleRepository1 == sampleRepository2);

        //(new Application(context)).run();
    }

}
```

위 코드를 통해 sampleRepository1과 sampleRepository2가 동일함을 확인할 수 있다.

따라서 아래와 같이 Singleton 객체 내에 공유해서는 안되는 필드가 존재하지 않도록 주의해야 한다.

```java
package com.sample.spring.repository;

public class Sample {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
```

```java
SampleRepository sampleRepository1 = context.getBean(SampleRepository.class);
SampleRepository sampleRepository2 = context.getBean(SampleRepository.class);

sampleRepository1.setName("Hong-Gil-Dong");
sampleRepository2.setName("Lee-Ji-Eun");

System.out.println(sampleRepository1.getName()); //Lee-Ji-Eun
System.out.println(sampleRepository2.getName()); //Lee-Ji-Eun
```

# 진짜 싱글톤일까?

XML로 작성된 설정의 경우 스프링이 파일을 읽고 그에 맞게 의존성이 주입된 객체를 생성하고, 한번 생성된 객체는 재사용되도록(Singleton 패턴이 유지되도록) 한다는 것이 크게 놀랍지는 않다.

우리가 설정한 그대로 객체를 만들어서 관리하면 되기 때문이다.

하지만 애노테이션의 경우 상황이 좀 다르다.

```java
@Configuration
public class Config {

    @Bean
    public Application application() {
        return new Application(primaryRepository());
    }

    @Bean
    public SampleRepository secondaryRepository() {
        return new SampleRepositoryImpl1();
    }

    @Bean
    @Primary
    public SampleRepository primaryRepository() {
        return new SampleRepositoryImpl2();
    }

}
```

우리는 이렇게 Config 파일을 작성했다.

그런데 어떻게 Spring은 이 코드에 의해 생성되는 객체들이 Singleton임을 보장해줄 수 있을까?

다시 말해, Application 객체에 주입된 SampleRepository와 getBean(”primaryRepository”) 으로 가져온 SampleRepository 가 정말 같을까?

확인을 위해 임시로 Applicaton에 getSampleRepository 메서드를 만들었다.

```java
public SampleRepository getSampleRepository() {
        return sampleRepository;
    }
```

```java
package com.sample.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        System.out.println(context.getBean(Application.class).getSampleRepository() == context.getBean("primaryRepository")); //true

        //context.getBean(Application.class).run();
    }

}
```

놀랍게도 두 객체는 같다.

# CBLIG을 사용한 바이트코드 조작

스프링은 CGLIB 이라는 바이트코드 조작 라이브러리를 이용해 설정파일을 감싸는 프록시 객체를 만든다.

```java
System.out.println(context.getBean(Config.class).getClass()); //class com.sample.spring.Config$$SpringCGLIB$$0
```

위 코드를 실행해보면 class com.sample.spring.Config 가 아닌 class com.sample.spring.Config$$SpringCGLIB$$0 가 출력된다.

Spring은 설정 파일을 Bean으로 등록할 때, 설정파일 자체를 등록하는 것이 아니라 CGLIB을 이용해 만들어진 프록시 객체를 등록한다.

Spring은 설정 파일의 바이트코드를 조작해, 반드시 객체 생성은 한번만 이루어짐을 강제로 보장한다.

![Untitled](7%20Singleton%20Pattern/Untitled.png)

Spring은 @Configuration 이 붙어있는 객체를 설정파일로 간주하고 CGLIB를 이용해 프록시 객체를 생성후 원래 객체 대신 Bean으로 등록한다.

따라서 반드시 설정파일에는 @Configuration 애노테이션을 붙여주어야 하며, 그렇지 않다면 프록시 객체가 생성되지 않아 Singleton 보장이 불가능하다.

@Configuration 애노테이션을 제거할 경우 아래 표현식의 값은 false가 된다.

```java
context.getBean(Application.class).getSampleRepository() == context.getBean("primaryRepository") //false
```

# Singleton이 아닌 Bean

객체를 Singleton으로 만들고 싶지 않다고 해서 @Configuration 을 붙이지 않는 것은 잘못된 방식이다. 

애노테이션을 제거하면 프레임워크는 해당 클래스가 설정 클래스임을 인식하지 못하고 그에 따른 오작동이 발생할 수 있다.

Spring에서는 Singleton Bean 외에도 매번 새로운 객체를 만드는 Prototype scope bean을 지원한다.

(그 외에도 Session scope bean, request scope bean 등을 지원하며 custom scope bean 도 만들 수 있다)

```java
@Bean
@Scope("prototype")
@Primary
public SampleRepository primaryRepository() {
    return new SampleRepositoryImpl2();
}
```

Bean 정의 시 위와 같이 Scope(org.springframework.context.annotation.Scope)를 지정해주면 singleton이 아닌 다른 scope의 빈을 만들 수 있다.

```java
context.getBean(Application.class).getSampleRepository() == context.getBean("primaryRepository") //false
```