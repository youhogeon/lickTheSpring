# 9. 의존관계 자동 주입 (@Autowired)

Spring context에서는 설정 파일을 통해 의존관계를 주입하는 방법 외에도 @Autowired 어노테이션을 통해 주입하는 방법을 지원한다.

# Setter 주입

```java
package com.sample.spring;

t.annotation.Bean;
t.annotation.Configuration;
t.annotation.Primary;

ry.SampleRepository;
ry.SampleRepositoryImpl1;
ry.SampleRepositoryImpl2;

@Configuration
public class Config {

    @Bean
    public Application application() {
        return new Application();
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

먼저, 생성자 주입을 사용하기 위해 Application이 빈 생성자를 사용하도록 변경하자.

그 다음, setSampleRepository 메서드를 만들어 외부로부터 setter를 통해 종속성을 주입받을 수 있도록 Application 클래스 코드를 변경했다.

```java
package com.sample.spring;

ry.SampleRepository;

public class Application {

    private SampleRepository sampleRepository;

    public void setSampleRepository(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

[5. 진정한 DI] 에서 setter 주입을 사용하는 경우 아래와 같이 빈 설정을 만들어서 의존성을 주입했었다.

```java
@Bean
public Application application() {
    Application app = new Application();
    app.setSampleRepository(primaryRepository());

    return app;
}
```

하지만, 이렇게 설정파일에서 setter를 사용하지 않고도 @Autowired 어노테이션을 통해 의존성을 주입받을 수 있다.

```java
package com.sample.spring;

factory.annotation.Autowired;

ry.SampleRepository;

public class Application {

    private SampleRepository sampleRepository;

    @Autowired
    public void setSampleRepository(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

setter에 @Autowired 어노테이션을 추가하는 경우, Spring은 빈 생성과정에서 해당 setter를 사용해 “자동으로” 의존성을 주입한다.

# 메서드 주입

```java
@Autowired
public void 아무렇게나지은메서드명(SampleRepository sampleRepository, DummyRepository dummyRepository) {
    this.sampleRepository = sampleRepository;
    this.dummyRepository = dummyRepository;
}
```

꼭 setter가 아니더라도 @Autowired 어노테이션을 이용하면 의존성을 주입받을 수 있으며, 여러개의 인자가 있는 경우에도 가능하다.

# 필드 주입

```java
package com.sample.spring;

factory.annotation.Autowired;

ry.SampleRepository;

public class Application {

    @Autowired
    private SampleRepository sampleRepository;

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

필드에 @Autowired 어노테이션을 붙여 주입받는 방법도 있다.

# @Inject 어노테이션

@Autowired 어노테이션 대신 @Inject 어노테이션(jakarta.injection.Inject)을 사용해도 똑같이 필드 주입이 가능하다.

더불어, @Inject 어노테이션은 JSR-330 표준으로 지정된 어노테이션이기 때문에 Spring 프레임워크가 아닌 다른 프레임워크로 교체해도 정상적으로 동작할 가능성이 높다.

그러나, @Autowired 어노테이션은 @Inject 어노테이션보다 더 많은 기능을 지원하기 때문에, 일반적으로 @Autowired 어노테이션을 사용한다.

```java
@Autowired(required=false)
private Main main; //해당하는 빈이 없어서 주입이 불가능한 경우 null
```

@Inject 어노테이션과 달리, @Autowired 어노테이션에는 required 값이 존재한다. 주입할 빈이 없는 경우에 Exception이 발생하는 것이 아니라 null이 주입된다.