# 5. 진정한 DI

지금까지 구현한 코드에는 사실 여러 문제가 있다.

- 코드가 Spring Framework에 지나치게 의존하고 있다. getBean 메서드가 제공되지 않는(다른 이름으로 제공되는) DI 프레임워크로 교체해야한다면 모든 코드를 다 바꾸어야 한다.
- 생성된 ApplicationContext를 계속 전달하며 공유해야 한다.

사실 지금까지는 의존성을 주입받았다고 보기는 어렵다. 

생성자를 통해 ApplicationContext를 주입 받았고, 그로부터 SampleRepository 타입 객체를 가져와 사용했을 뿐이다.

# 사전 준비

그 전에 먼저, 코드를 간소화하기 위해 Application class도 Bean으로 등록시켜주자.

```java
package com.sample.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        context.getBean(Application.class).run();
    }

}
```

```java
package com.sample.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.sample.spring.repository.SampleRepository;
import com.sample.spring.repository.SampleRepositoryImpl1;
import com.sample.spring.repository.SampleRepositoryImpl2;

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

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;

public class Application {

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
        //SampleRepository를 어떻게 주입받지?
    }

}
```

# 의존성 주입

위와 같이 코드를 바꾸면 Application class는 SampleRepository를 어떻게 주입받아올 수 있을까?

답은 간단하다. 생성자 또는 매개변수를 통해 주입받아오면 된다.

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;

public class Application {

    private final SampleRepository sampleRepository;

    public Application(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public void run() {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

이제, Bean 설정을 다시 바꾸어보자.

```java
@Bean
public Application application() {
    return new Application(primaryRepository());
}
```

이제 완벽하다.

- Application은 더 이상 어떠한 구체 클래스(SampleRepositoryImpl1, SampleRepositoryImpl2)에도 의존하지 않는다.
- 모든 객체 간 의존성은 Config 또는 XML 파일을 통해 외부에서 관리할 수 있다. (사용하는 repository 구현체가 바뀌어도 설정만 바꾸면 동작한다.)

XML의 경우 constructor-arg 태그를 통해 생성자에 의존관계를 지정할 수 있다.

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="application" class="com.sample.spring.Application">
        <constructor-arg ref="primaryRepository" />
    </bean>

    <bean id="secondaryRepository" class="com.sample.spring.repository.SampleRepositoryImpl1" />
    <bean id="primaryRepository" class="com.sample.spring.repository.SampleRepositoryImpl2" />

</beans>
```

# setter를 사용한 주입

생성자를 통해 주입하지 않고, setter를 사용해 주입하는 방법도 있다.

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;

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

이 경우, Bean 설정은 아래와 같이 하면 된다.

(의존관계를 알아서 잘 설정해서 return 해주면 된다.)

```java
		@Bean
    public Application application() {
        Application app = new Application();
        app.setSampleRepository(primaryRepository());

        return app;
    }
```

XML의 경우 property 태그를 통해 의존관계를 설정할 수 있다.

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="application" class="com.sample.spring.Application">
        <property name="sampleRepository" ref="primaryRepository" />
    </bean>

    <bean id="secondaryRepository" class="com.sample.spring.repository.SampleRepositoryImpl1" />
    <bean id="primaryRepository" class="com.sample.spring.repository.SampleRepositoryImpl2" />

</beans>
```

# 생성자 주입 vs Setter 주입

생성자 주입은 필드를 final로 지정해 불변성을 보장할 수 있다는 점에서 좋다.

Spring 공식 문서에서도 생성자 주입을 권장한다.

그러나, 생성자 인수가 너무 많은 경우나 선택적 종속성이 필요한 경우에는 setter 주입을 사용하는 것이 좋을 수 있다.

# 코드

[https%3A%2F%2Fgithub.com%2Fyouhogeon%2FlickTheSpring%2Ftree%2F456e40e5fbf1648027d51831e00e5f0d1ecd0b00](https://github.com/youhogeon/lickTheSpring/tree/456e40e5fbf1648027d51831e00e5f0d1ecd0b00)