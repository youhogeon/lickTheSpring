# 2-1. Embeded Tomcat 사용하기

# 개요

매 번 코드 수정사항이 있을 때 마다 war로 패키징하고 Tomcat 경로에 war 파일을 넣고 Tomcat을 재시작 하는 일은 번거롭다.

다행히 Tomcat은 Embeded Tomcat을 지원하여 프로젝트 내에 Tomcat을 포함할 수 있다.

# pom.xml 의존성 추가

```java
<dependency>
  <groupId>org.apache.tomcat.embed</groupId>
  <artifactId>tomcat-embed-core</artifactId>
  <version>${tomcat.version}</version>
  <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <version>${tomcat.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-jasper</artifactId>
    <version>${tomcat.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-jasper-el</artifactId>
    <version>${tomcat.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-jsp-api</artifactId>
    <version>${tomcat.version}</version>
    <scope>provided</scope>
</dependency>
```

위와 같이 다섯 개의 의존성을 추가해주어야 한다.

빌드 시 포함시키지 않을 것이기 때문에 scope를 provided로 설정했다.

```java
<properties>
  ...
  <tomcat.version>10.1.4</tomcat.version>
</properties>
```

properties 에 사용할 Tomcat 버전을 명시해준다.

# main 메서드 작성

이제, main 메서드에 Embeded Tomcat을 구동하는 코드를 넣으면 된다.

```java
package com.lickthespring.web;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class TomcatLauncher {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context ctx = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
        WebResourceRoot resources = new StandardRoot(ctx);
        WebResourceSet webResourceSet = new DirResourceSet(resources, "/WEB-INF/classes", new File("target/classes").getAbsolutePath(), "/");
        resources.addPreResources(webResourceSet);
        ctx.setResources(resources);

        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
    }
}
```

이제, F5를 눌러 우리의 프로젝트 내에서 Tomcat을 실행할 수 있다.

기존에 사용하던 외장 Tomcat처럼 내장 Tomcat도 모든 클래스를 뒤져 @WebServlet 어노테이션을 가진 클래스를 찾아 Servlet으로 등록한다.

# Spring Boot의 내장 Tomcat

많은 사람들이 Spring Boot 자체를 웹서버라고 오해한다.

(Spring Web MVC 핥아먹기 시리즈이기 때문에, 아직 Spring Boot에 대해 설명한 적은 없다.)

그러나 Spring Boot는 방금 우리가 한 것 처럼 Tomcat(혹은 undertow 등 여러 WAS)을 내장하고 있을 뿐이다.

Spring Boot을 사용하면 내장 WAS를 통해 개발 편의성을 높일 수 있고, 내장 WAS를 묶어서 빌드함으로써 배포의 편의성도 높일 수 있다.