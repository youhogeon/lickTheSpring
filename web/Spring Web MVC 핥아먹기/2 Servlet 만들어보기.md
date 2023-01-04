# 2. Servlet 만들어보기

# 개요

이제 HttpServlet을 상속받아 우리의 비즈니스 로직을 처리하는 Servlet을 만들고 WAS에게 처리하도록 하면 HTTP 애플리케이션이 완성된다.

# 프로젝트 생성

```java
mvn archetype:generate
```

위 명령을 입력해 빈 프로젝트를 생성해주자.

```java
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.lickthespring.web</groupId>
  <artifactId>web</artifactId>
  <version>1.0-SNAPSHOT</version>
  <name>web</name>
  <packaging>war</packaging>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

    <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>6.0.0</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>

  <build>
		<plugins>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.6.1</version>
				<configuration>
					<source>1.8</source>
					<target>1.8</target>
				</configuration>
			</plugin>
			<plugin>
				<artifactId>maven-war-plugin</artifactId>
				<version>3.3.2</version>
        <configuration>
         <webXml>src/main/webapp/WEB-INF/web.xml</webXml>
         <failOnMissingWebXml>false</failOnMissingWebXml>
        </configuration>
			</plugin>
		</plugins>
	</build>

</project>
```

위와 같이 pom.xml을 수정해 주었다.

- WAS는 jar이 아닌 war 형식의 파일을 요구하기 때문에, <packaging>war</packaging> 을 추가하였다.
- Servlet API를 사용하기 위해 jakarta.servlet-api를 의존성에 추가하였다.
- maven-war-plugin은 web.xml 파일이 없으면 오류가 발생하므로, web.xml 파일이 없어도 동작하도록 <failOnMissingWebXml>false</failOnMissingWebXml> 를 추가하였다.

# 톰캣 내려받기

가장 대중적으로 사용되는 WAS인 톰캣을 사용하자.

톰캣 공식 홈페이지에서 Core(zip) 을 내려받으면 된다.

[https%3A%2F%2Ftomcat.apache.org%2Fdownload-10.cgi](https://tomcat.apache.org/download-10.cgi)

압축을 풀고 bin 폴더 내의 startup.bat (startup.sh)을 실행하면 톰캣이 실행된다.

[http%3A%2F%2Flocalhost%3A8080](http://localhost:8080) 접속했을 때 귀여운 고양이가 나오면 정상적으로 설치된 것이다.

# Servlet 만들기

HttpServlet (jakarta.servlet.http.HttpServlet)을 상속받는 클래스를 만들자.

```java
package com.lickthespring.web;

import jakarta.servlet.http.HttpServlet;

public class SampleServlet extends HttpServlet {

}
```

이렇게만 해도 해당 클래스는 Servlet으로 동작한다.

HttpServlet이 doGet, doPost, doPut, doDelete 등을 기본으로 구현해두었기 때문이다.

```java
// HttpServlet에 구현된 doGet 메서드
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String protocol = req.getProtocol();
    String msg = lStrings.getString("http.method_get_not_supported");
    resp.sendError(getMethodNotSupportedCode(protocol), msg);
}
```

이 메서드를 Override해서 비즈니스 로직을 추가하자.

확인을 위해 init 메서드(Servlet이 생성될 때 호출됨)도 Override 하였다.

```java
package com.lickthespring.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SampleServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("Servlet Initialized!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        System.out.println("GET Request from " + requestURI);

        //DO SOMETHING

        resp.addHeader("content-type", "text/plain");
        resp.setStatus(200);

        PrintWriter writer = resp.getWriter();
        writer.println("Hello Servlet World!");
    }

}
```

이제 우리의 비즈니스 Servlet이 완성되었다.

그런데 이 Servlet을 WAS(톰캣)에게 어떻게 전달할 수 있을까?

다시 말해 톰캣은 이 class가 Servlet임을 어떻게 인지할 수 있을까?

# web.xml을 이용하는 방법

web.xml 파일에 서블릿 정보들을 등록해두면, 톰캣은 web.xml이라는 파일을 읽고 서블릿을 등록한다.

src/main/webapp/WEB-INF/web.xml 파일을 만들고 아래와 같이 입력하자.

(경로는 pom.xml에서 maven-war-plugin을 설정에서 설정하였다.)

```java
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">
    <servlet>
        <servlet-name>sampleServlet</servlet-name>
        <servlet-class>com.lickthespring.web.SampleServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>sampleServlet</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
</web-app>
```

이렇게 서블릿을 등록하고 서블릿에 mapping될 URL을 설정해주면 톰캣이 서블릿을 인식할 수 있다.

# 어노테이션을 이용하는 방법

Servlet 클래스에 @Servlet 어노테이션을 붙이면 WAS는 해당 클래스를 Servlet으로 인식한다.

이는 Jakarta Servlet 표준 사양이다.

```java
//...
import jakarta.servlet.annotation.WebServlet;

@WebServlet(value = "/*")
public class SampleServlet extends HttpServlet {
	...
}
```

# 톰캣에 배포하기

어노테이션을 이용해도 되고, web.xml을 이용해도 된다. 심지어 두 가지 방법을 함께 사용할 수도 있다.

Servlet이 완성되었으면 아래 명령어를 입력해 war파일을 생성하자.

```java
mvn clean package
```

프로젝트 루트의 target 폴더를 보면, web-1.0-SNAPSHOT.war 파일이 생성되었다.

이를 아까 내려받은 톰캣의 webapps 폴더에 ROOT.war 이라는 이름으로 붙여넣자.

이제 톰캣을 실행하면(이미 실행중이라면 포트 충돌이 발생하므로 종료 후 재실행 해야 함), 우리의 Servlet을 만날 수 있다.

[http%3A%2F%2Flocalhost%3A8080%2F](http://localhost:8080/)

# 작동 확인하기

첫 접속시 콘솔에 Servlet Initialized! 와 GET Request from / 가 표시되며, 브라우저에는 Hello Servlet World! 가 나타난다.

![Untitled](2%20Servlet%20%EB%A7%8C%EB%93%A4%EC%96%B4%EB%B3%B4%EA%B8%B0%2FUntitled.png)

브라우저 개발자도구를 이용해 자세히 살펴보면, 우리가 입력한 응답헤더(status code, content-type)이 브라우저로 잘 전달되었음을 확인할 수 있다.

Connection, Content-Length 등의 헤더는 WAS가 자동으로 생성해준다.

이후, 여러번 접속을 반복하면 Servlet Initialized! 는 더 이상 보이지 않고 Get Request from / 이 반복적으로 출력된다.

기본적으로 Servlet은 싱글톤으로 동작하기 때문에, 첫 접속 시에만 초기화가 일어나기 때문이다.

(싱글톤이기 때문에 공유 필드 사용 시 주의해야 한다.)

초기화 작업이 무거운 작업인 경우 첫 접속자는 접속까지 오랜 시간을 기다려야 하므로, load on startup 옵션을 이용해 WAS가 시작될 때 초기화가 이루어지도록 설정할 수도 있다.

```java
@WebServlet(value = "/*", loadOnStartup = 1)
public class SampleServlet extends HttpServlet {
	//...
}
```

# 코드

본 글에 사용한 코드는 아래 레포지토리에서 볼 수 있다.

[https%3A%2F%2Fgithub.com%2Fyouhogeon%2FlickTheSpring%2Ftree%2F58fb97988969af9c6b6f1172153bfdfc7db01552%2Fweb](https://github.com/youhogeon/lickTheSpring/tree/58fb97988969af9c6b6f1172153bfdfc7db01552/web)