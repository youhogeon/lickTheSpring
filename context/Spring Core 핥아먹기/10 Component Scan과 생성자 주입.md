# 10. Component Scan과 생성자 주입

# ComponentScan

만약 프로그램의 규모가 커져서 등록해야 할 빈이 수백, 수천개라면 어떻게 해야할까?

의존관계 설정파일을 관리하는데 어머어머한 시간이 소요될 것이다.

Spring context에서는 빈을 자동으로 찾아 등록(주입이 아니다)하는 Component scan 기능을 제공한다.

설정 파일에 @ComponentScan 어노테이션을 붙임으로써, Component scan을 사용할 수 있다.

```java
package com.sample.spring;

t.annotation.ComponentScan;
t.annotation.Configuration;

@Configuration
@ComponentScan
public class Config {
    //기존의 빈 등록 설정은 모두 삭제
}
```

Spring 컨테이너가 만들어지면 인자로 받은 설정 클래스 객체는 자동으로 빈으로 등록된다. (정확히는 CGLIB에 의해 생성된 프록시 객체가 등록된다)

이 과정에서, 설정 클래스에 @ComponentScan 어노테이션이 있으면, Component scan 기능이 동작한다.

@ComponentScan은 해당 패키지와 하위 패키지 내의 모든 클래스를 살펴보고 @Component 어노테이션이 붙어있는 클래스를 빈으로 등록한다.

이 과정에서 @Autowired 어노테이션이 붙어있는 생성자, 수정자, 필드, 메서드가 있다면 빈 생성 후 의존관계도 주입한다. (생성자의 경우 생성과 동시에 주입)

따라서 코드가 올바르게 동작하려면 빈으로 등록하고자 하는 클래스에 @Component를 붙여야 한다.

```java
package com.sample.spring;

factory.annotation.Autowired;
type.Component;

ry.SampleRepository;

@Component
public class Application {

    @Autowired
    private SampleRepository sampleRepository;

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

```java
ory;

type.Component;

@Component
public class SampleRepositoryImpl1 implements SampleRepository {

    public int getLastUserId() {
        return 100;
    }

}
```

```java
ory;

t.annotation.Primary;
type.Component;

@Component
@Primary
public class SampleRepositoryImpl2 implements SampleRepository {

    public int getLastUserId() {
        return 200;
    }

}
```

SampleRepository 타입의 빈이 두 개 등록되기 때문에, @Primary 어노테이션을 SampleRepositoryImpl2 클래스에 붙여 우선 순위를 가지고 주입되도록 하였다.

# @Service, @Repository, @Controller

@Service, @Repository, @Controller 어노테이션은 해당 클래스가 각각 서비스, 레포지토리, 컨트롤러 임을 표시하기 위해 사용하는 어노테이션이다.

각 어노테이션을 붙이면 spring에서(spring context 이외에 다른 spring projects) 해당 빈을 등록할 때 추가적인 작업을 함께 해준다.

@Repository를 붙인 클래스의 경우 데이터베이스 CheckedException이 발생하면 Spring의 UncheckedException으로 변환해주는 기능이 추가되고, @Controller를 붙인 클래스의 경우 spring web mvc에서 @RequestMapping이 가능하도록 한다.

아직까지 @Service는 아무 동작도 하지 않지만 추후 추가될 기능을 대비해, 그리고 코드의 가독성을 위해 사용한다.

이 어노테이션들은 @Component 어노테이션을 포함하기 때문에, @Component 어노테이션을 추가로 붙일 필요가 없다. 각 어노테이션의 정의는 아래와 같다.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
	@AliasFor(annotation = Component.class)
	String value() default "";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
	@AliasFor(annotation = Component.class)
	String value() default "";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
	@AliasFor(annotation = Component.class)
	String value() default "";
}
```

우리의 (아무런 기능도 없는) 두 Repository도 @Repository 어노테이션으로 변경하자.

```java
ory;

type.Repository;

@Repository
public class SampleRepositoryImpl1 implements SampleRepository {

    public int getLastUserId() {
        return 100;
    }

}
```

```java
ory;

t.annotation.Primary;
type.Repository;

@Repository
@Primary
public class SampleRepositoryImpl2 implements SampleRepository {

    public int getLastUserId() {
        return 200;
    }

}
```

# ComponentScan 설정

기본적으로 Component scan은 해당 패키지와 하위 패키지 내의 모든 클래스를 살펴보고 @Component 어노테이션이 붙어있는 클래스를 빈으로 등록한다.

하지만, base package를 지정해 더 상위 혹은 하위의 패키지만 스캔하도록 변경할 수 있다.

또, 필터를 통해 특정 어노테이션 포함 여부, 정규식, 커스텀 필터 등을 지정해 스캔되도록 설정할 수 있다.

[https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-scanning-filters](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-scanning-filters)

```java
@Configuration
@ComponentScan(basePackages = "org.example",
        includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
        excludeFilters = @Filter(Repository.class))
public class AppConfig {
    // ...
}
```

# 생성자 주입

수정자(setter), 메서드, 필드 주입 외에도 생성자를 통해 의존 관계를 주입받을 수 있다.

(ComponentScan을 사용하지 않으면 생성자 주입이 까다롭기 때문에 설명하지 않았었다.)

```java
package com.sample.spring;

factory.annotation.Autowired;
type.Component;

ry.SampleRepository;

@Component
public class Application {

    private final SampleRepository sampleRepository;

    @Autowired
    public Application(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

심지어 Spring context 4.3버전 부터는, 생성자가 하나인 경우에 @Autowired 어노테이션 없이 생성자로 의존관계가 주입된다.

```java
package com.sample.spring;

type.Component;

ry.SampleRepository;

@Component
public class Application {

    private final SampleRepository sampleRepository;

		//@Autowired
    public Application(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

# @Value

@Value 어노테이션을 통해 빈이 아닌 “값”을 주입할 수 있다.

이렇게 주입된 값은 자료형에 맞추어 자동 변환된다.

```java
public Application(SampleRepository sampleRepository, @Value("Hello, World!") String strTest, @Value("10") int intTest, @Value("true") boolean boolTest, @Value("10.5") double doubleTest) {
    System.out.println(strTest); //"Hello, World!"
    System.out.println(intTest); //10
    System.out.println(boolTest); //true
    System.out.println(doubleTest); //10.5
    this.sampleRepository = sampleRepository;
}
```

또, @Value 어노테이션은 스프링 표현식(SpEL)을 지원한다.

[https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)

```java
private final SampleRepository sampleRepository;

public Application(SampleRepository sampleRepository, @Value("#{new String('hello world').toUpperCase()}") String strTest) {
    System.out.println(strTest); //HELLO WORLD
    this.sampleRepository = sampleRepository;
}
```

# @Resource

@Resource 어노테이션(jakarta.annotation.Resource)은 @Autowired와 유사하게 동작한다.

@Autowired가 타입을 통해 빈을 찾는 반면에, @Resource는 이름을 통해 빈을 찾아 주입한다.

# 생성자 주입 vs 수정자 주입 vs 필드 주입

스프링에서는 공식적으로 생성자 주입을 권장한다.

- 필드를 final로 설정해 필드에 주입된 의존관계의 불변성을 보장할 수 있다. (수정자 주입은 setter가 public으로 열려있어야 해서 다른 개발자가 setter를 사용해 불변성을 깨트릴 가능성이 있다.)
- 단위테스트 시 생성자주입이 테스트에 용이하다. (필드 주입은 필드가 private인 경우 값을 변경할 수 없어 테스트 시 수동 주입이 불가능하다.)