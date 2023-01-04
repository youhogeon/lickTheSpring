# 3. Spring Context 사용해보기

Spring의 IoC 기능은 org.springframework:spring-context 의존성으로부터 얻을 수 있음. 

spring-context 가 의존하는 패키지를 보면 아래와 같음

![Untitled](3%20Spring%20Context%20사용해보기/Untitled.png)

따라서 spring-context만 의존성에 추가하면 spring-core, spring-aop 등을 추가하지 않고 사용 가능하다.

# Maven project 생성

```java
mvn archetype:generate
```

위 명령을 통해 비어있는 Maven Project를 생성할 수 있다.

Maven과 같은 빌드 도구를 사용하지 않으려면 spring-context.jar 파일을 내려받고

javac -cp “spring-context.jar” [xxxx.java](http://xxxx.java) 와 같이 컴파일 해야 한다.

뿐만 아니라 spring-context가 의존하는 다른 패키지들을 다 찾아서 내려받고 javac에 명령줄 인자로 함께 넘겨야 한다.

의존성을 자동으로 관리해주는 Maven, Gradle과 같은 빌드 도구를 사용하지 않을 이유가 없다.

# spring-context 의존성 추가

pom.xml 파일의 dependencies 태그 내에 아래 의존성을 추가하면, spring-context 의존성이 추가된다.

```java
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.0.3</version>
</dependency>
```

![Untitled](3%20Spring%20Context%20사용해보기/Untitled%201.png)

spring-context만 추가하였음에도 불구하고, spring-context가 의존하는 다른 의존성도 함께 추가된다.

# Spring 없는 DI

```java
package com.sample.spring;

public class Main {

    public static void main(String[] args) {
        (new Application()).run();
    }

}
```

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;
import com.sample.spring.repository.SampleRepositoryImpl1;

public class Application {
    
    public void run() {
        SampleRepositoryImpl1 sampleRepository = new SampleRepositoryImpl1();

        System.out.println(sampleRepository.getLastUserId());
    }

}
```

```java
package com.sample.spring.repository;

public class SampleRepositoryImpl1 {

    public int getLastUserId() {
        return 100;
    }

}
```

Application은 SampleRepositoryImpl1에 강하게 의존한다.

만약 기존의 SampleRepositoryImpl1를 놔두고 다른 DBMS를 사용하는 SampleRepositoryImpl2를 사용해야한다면, 모든 코드를 뒤져가며 코드를 변경해야 할 것이다. (OCP, DIP 원칙 등에 위배)

구체 클래스가 아니라 추상화에 의존하도록 코드를 바꾸어보자.

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;
import com.sample.spring.repository.SampleRepositoryImpl1;

public class Application {
    
    public void run() {
        SampleRepository sampleRepository = new SampleRepositoryImpl1();

        System.out.println(sampleRepository.getLastUserId());
    }

}
```

```java
package com.sample.spring.repository;

public interface SampleRepository {
    
    int getLastUserId();

}
```

```java
package com.sample.spring.repository;

public class SampleRepositoryImpl1 implements SampleRepository {

    public int getLastUserId() {
        return 100;
    }

}
```

```java
package com.sample.spring.repository;

public class SampleRepositoryImpl2 implements SampleRepository {

    public int getLastUserId() {
        return 200;
    }

}
```

Application은 SampleRepositoryImpl1로부터 자유로운가?

인터페이스를 사용했음에도 여전히 new SampleRepositoryImpl1() 와 같은 코드가 남아있다.

구체 클래스 의존성을 끊으려면, 생성된 객체를 외부로부터 주입받아야 한다.

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;

public class Application {

    public void run(SampleRepository sampleRepository) {
        System.out.println(sampleRepository.getLastUserId());
    }

}
```

```java
package com.sample.spring;

import com.sample.spring.repository.SampleRepository;
import com.sample.spring.repository.SampleRepositoryImpl1;

public class Main {

    public static void main(String[] args) {
        SampleRepository sampleRepository = new SampleRepositoryImpl1();

        (new Application()).run(sampleRepository);
    }

}
```

그러나 이런 방식은 상당히 번거롭다.

구체 클래스 객체들을 생성해서 주입하는 코드를 따로 작성해야 하고

생성된 객체들을 개발자가 직접 관리해야 한다.

스프링을 사용하면 스프링에게 객체를 생성과 관리를 위임하고, 스프링으로부터 객체를 주입받아올 수 있다.

# Spring Context 사용해보기

Spring이 관리하는 객체를 Bean(Spring Bean)이라고 부른다.

Spring Context의 ApplicationContext를 사용하면 빈을 등록하고 받아올 수 있다.

```java
package com.sample.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("services.xml");

        (new Application(context)).run();
    }

}
```

```java
package com.sample.spring;

import org.springframework.context.ApplicationContext;

import com.sample.spring.repository.SampleRepository;

public class Application {

    private ApplicationContext context;

    public Application(ApplicationContext context) {
        this.context = context;
    }

    public void run() {
        SampleRepository sampleRepository = context.getBean(SampleRepository.class);

        System.out.println(sampleRepository.getLastUserId());
    }

}
```

```java
**src/main/resources/services.xml**

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="sampleRepository" class="com.sample.spring.repository.SampleRepositoryImpl1" />

</beans>
```

Bean으로 등록하고자 하는 구체 클래스의 정보를 services.xml라는 설정 파일에 XML 형식으로 작성하였다.

ApplicationContext 인터페이스의 구현체인 ClassPathXmlApplicationContext 를 생성해 services.xml에 작성된 빈 정보를 불러왔고, 객체를 생성했다.

ApplicationContext 의 getBean 메서드를 이용하면(정확히는 하위 인터페이스인 BeanFactory의 getBean) 인터페이스를 지정해 구체 클래스 객체를 받아올 수 있다.

이제, Application은 어떠한 구체 클래스에도 의존하지 않는다. SampleRepositoryImpl1을 SampleRepositoryImpl2로 변경하고 싶다면, services.xml을 아래와 같이 바꿔주기만 하면 된다.

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="sampleRepository" class="com.sample.spring.repository.SampleRepositoryImpl2" />

</beans>
```