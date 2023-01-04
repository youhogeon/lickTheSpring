# 6. DispatcherServlet 사용 및 MVC 패턴 구현

# 개요

이제 Spring Web MVC 공식 문서를 보기 위한 사전 지식을 다 갖추었다.

[https://docs.spring.io/spring-framework/docs/current/reference/html/web.html](https%3A%2F%2Fdocs.spring.io%2Fspring-framework%2Fdocs%2Fcurrent%2Freference%2Fhtml%2Fweb.html%23mvc-servlet)

# DispatcherServlet을 사용하는 두 가지 방법

DispatcherServlet을 WAS가 servlet으로 인식할 수 있도록 하는 방법은 두가지가 있다.

지난 글에서는 web.xml에 DispatcherServlet을 등록하고, DispatcherServlet의 init-param으로 bean 설정이 담긴 xml파일 경로를 넘겨 application context를 내부적으로 생성하는 방법을 설명했다.

하지만, @Servlet 어노테이션을 도입하며 기껏 없애놓은 web.xml을 다시 만드는 것은 너무나도 번거롭다.

@Servlet 어노테이션과 함께 Servlet 3.0 사양에 추가된 pluggability mechanism (JSR-315)을 이용하면 web.xml 없이 DispatcherServlet을 사용할 수 있다.

구체적인 작동 과정은 다음과 같다.

1. WAS는 서버가 부팅될 때 jakarta.servlet.ServletContainerInitializer 를 구현하는 클래스의 onStartup 메서드를 호출한다. (Servlet 3.0 사양)
2. 이 때, 구현 클래스에 @HandlerTypes(classtype) 어노테이션이 있으면 classtype에 해당하는 모든 클래스를 찾아 onStartup의 인자로 넘겨준다.
3. Spring에는 ServletContainerInitializer를 구현한 org.springframework.web.SpringServletContainerInitializer 가 있고, @HandlerTypes(WebApplicationInitializer.class) 어노테이션을 가지고 있다.
4. 따라서 WAS는 SpringServletContainerInitializer의 onStartup 메서드를 호출하면서 org.springframework.web.WebApplicationInitializer 인터페이스를 구현한 모든 클래스를 인자로 넘겨준다.
5. Spring은 onStartup 메서드 내부에서, 인자로 넘겨받은 클래스들을 객체화 하여 각각의 onStartup 메서드를 호출한다.

따라서 우리가 WebApplicationInitializer 인터페이스를 상속받고 onStartup 메서드를 구현하면, WAS 부팅 시 실행될 코드를 작성할 수 있다.

# WebApplicationInitializer 구현 클래스 만들기

```java
package com.lickthespring.web;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        DispatcherServlet servlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}
```

위에서 설명한 대로 WebApplicationInitializer 를 구현한 코드이다.

이제, 지난 글에서 만든 web.xml과 spring-servlet.xml 을 삭제해도 잘 동작한다.

onStartup에서는 ApplicationContext를 만들고(Spring Core 핥아먹기에서 정말 많이 다룬 내용이다) 만들어진 context를 DispatcherServlet 객체를 생성하며 주입한다.

이후 onStartup의 인자로 넘겨받은(WAS로부터 Spring으로 넘겨지고 다시 우리에게 넘겨진) servletContext에 DispatcherServlet 객체를 등록한다.

Spring web mvc를 사용하지 않는 비 웹 애플리케이션에서 main 메서드에 시작 코드를 작성하는 것 처럼, onStartup 메서드에 시작 코드(ApplicationContext, DispatcherServlet 생성과 serlvet 등록)를 작성하면 된다.

# WebApplicationContext

MyWebApplicationInitializer 코드를 유심히 보면 ApplicationContext가 아니라 WebApplicationContext를 사용하였음을 볼 수 있다.

WebApplicationContext는 ApplicationContext를 확장해 getServletContext() 등의 추가 기능을 제공한다.

여러개의 WebApplicationContext와 DispatcherServlet을 생성해 (여러 서비스가)각각 독립적으로 동작하도록 context를 설계할 수도 있다.

여기에 공통 Context를 사용해 여러 곳(여러 서비스)에서 사용할 공통 빈은 공통 Context에 등록하고, 개별적으로(개별 서비스에서) 사용할 빈은 각각의 WebApplicationContext에 등록하는 구조를 가질수도 있다.

이러한 구조를 AbstractAnnotationConfigDispatcherServletInitializer (WebApplicationInitializer를 구현한 추상 클래스)를 통해 쉽게 만들 수 있다.

```java
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class };
        //비워두면 root context 없이 단일 contex로 사용 가능
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { App1Config.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/app1/*" };
    }
}
```

![Untitled](6%20DispatcherServlet%20%EC%82%AC%EC%9A%A9%20%EB%B0%8F%20MVC%20%ED%8C%A8%ED%84%B4%20%EA%B5%AC%ED%98%84%2FUntitled.png)

# CommentController 구현

자 이제, 모든 요청은 DispatcherServlet이 받도록 하고, Spring Web MVC를 이용해 Controller를 만들어보자.

```java
package com.lickthespring.web.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {

    private final CommentService commentService = CommentService.getInstance();
    
    @RequestMapping("/")
    public String index() {
        return "/WEB-INF/views/index.jsp";
    }

    @GetMapping("/comments")
    //same as @RequestMapping(value="/comments", method=RequestMethod.GET)
    public String getAllComments() {
        return "/WEB-INF/views/comments.jsp";
    }

    @PostMapping(value="/comments")
    public void createComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Comment comment = new Comment(request.getParameter("name"), request.getParameter("comment"));

        commentService.save(comment);

        response.sendRedirect("/comments");
    }

    @GetMapping("/comments/*")
    public String getAllComments(HttpServletRequest req, HttpServletResponse resp) {
        String[] path = req.getRequestURL().toString().split("/");
        int id = Integer.parseInt(path[path.length - 1]);

        req.setAttribute("id", id);

        return "/WEB-INF/views/comment.jsp";
    }

}
```

각 Servlet들 내의 코드를 그대로 옮겼다. (기존 Servlet은 삭제하였다.)

RequestMappingHandlerAdapter는 문자열을 받으면 해당 경로의 jsp파일을 ModelAndView 타입으로 변환해 View Resolver에게 전달해준다.

코드에 사용된 @GetMapping, @PostMapping 어노테이션은, 메타 어노테이션으로 @RequestMapping(method=RequestMethod.GET), @RequestMapping(method=RequestMethod.POST) 와 동일하다.

마찬가지로 @PutMapping, @DeleteMapping, @PatchMapping 어노테이션도 존재한다.

# 뷰 개선하기

뷰(JSP)에 작성된 비즈니스 코드를 컨트롤러로 옮겼다.

```java
package com.lickthespring.web.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentController {

    private final CommentService commentService = CommentService.getInstance();
    
    @RequestMapping("/")
    public String index() {
        return "/WEB-INF/views/index.jsp";
    }

    @GetMapping("/comments")
    //same as @RequestMapping(value="/comments", method=RequestMethod.GET)
    public String getAllComments(Model model) {
        List<Comment> comments = commentService.findAll();

        int count = commentService.count();

        model.addAttribute("comments", comments);
        model.addAttribute("count", count);

        return "/WEB-INF/views/comments.jsp";
    }

    @PostMapping(value="/comments")
    public void createComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Comment comment = new Comment(request.getParameter("name"), request.getParameter("comment"));

        commentService.save(comment);

        response.sendRedirect("/comments");
    }

    @GetMapping("/comments/*")
    public String getAllComments(HttpServletRequest req, HttpServletResponse resp) {
        String[] path = req.getRequestURL().toString().split("/");
        int id = Integer.parseInt(path[path.length - 1]);

        Comment comment = null;

        try {
            comment = commentService.findById(id - 1);
        } catch (IllegalArgumentException e) {
            resp.setStatus(404);
        }

        req.setAttribute("comment", comment);

        return "/WEB-INF/views/comment.jsp";
    }

}
```

Model 혹은 HttpServletRequest를 이용해 뷰에 전달할 데이터를 추가하였다.

```java
<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.lickthespring.web.entity.Comment" %>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>전체 방명록</title>
    </head>
    <body>
        <h1>전체 방명록</h1>
        <div style="margin-bottom: 10px;"><a href="/">메인으로</a></div>

        <%
            List<Comment> comments = (List<Comment>)request.getAttribute("comments");
            int count = (int) request.getAttribute("count");

            for (int id = 0; id < count; id++) {
                Comment comment = comments.get(id);
        %>
        <div style="border:1px solid #CCC; margin:5px 10px; padding:10px;">
            <div><%=(id+1)%> / ${count}</div>
            <div style="font-weight:bold"><%=comment.getName()%></div>
            <div><%=comment.getComment()%></div>
        </div>
        <%
            }
        %>
    </body>
</html>
```

comments.jsp는 위와 같다.

```java
<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.lickthespring.web.entity.Comment" %>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>방명록 상세보기</title>
    </head>
    <body>
        <h1>방명록 상세보기</h1>
        <div style="margin-bottom: 10px;"><a href="/">메인으로</a></div>

        <%
            Comment comment = (Comment) request.getAttribute("comment");
            if (comment != null) {
        %>
        <div style="border:1px solid #CCC; margin:5px 10px; padding:10px;">
            <div style="font-weight:bold"><%=comment.getName()%></div>
            <div><%=comment.getComment()%></div>
        </div>
        <% } else { %>
        <div>
            존재하지 않는 방명록입니다.
        </div>
        <% } %>
</html>
```

comment.jsp는 위와 같다.

# Service, Repository 빈 등록하기

마지막으로, CommentService와 CommentRepository도 CommentController처럼 빈으로 등록하기 위해 각각 @Service와 @Repository 어노테이션을 추가하였다.

더불어, 싱글톤을 생성하기 위한 코드를 삭제하였다.

```java
@Controller
public class CommentController {

    private final CommentService commentService;
    
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping("/")
    public String index() {
        return "/WEB-INF/views/index.jsp";
    }

    //이 아래부터는 동일함

}
```

```java
@Repository
public class CommentRepository {
    
    private final static List<Comment> comments = new ArrayList<>();

    public Comment findById(int id) { ... }

    //이 아래부터는 동일함

}
```

```java
@Service
public class CommentService {
    
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment findById(int id) { ... }

    //이 아래부터는 동일함

}
```

# 코드

지금까지 구현한 코드는 아래 저장소에서 볼 수 있다.

[https%3A%2F%2Fgithub.com%2Fyouhogeon%2FlickTheSpring%2Ftree%2F73f3baffdfb9f575322ee1762efb9ef164e9d51b%2Fweb](https://github.com/youhogeon/lickTheSpring/tree/73f3baffdfb9f575322ee1762efb9ef164e9d51b/web)