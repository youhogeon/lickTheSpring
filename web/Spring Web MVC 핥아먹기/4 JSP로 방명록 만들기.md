# 4. JSP로 방명록 만들기

# 개요

Servlet에 HTML 코드가 섞여있으면 가독성도 떨어지고 유지보수 문제도 발생한다.

따라서 템플릿 엔진을 이용해 코드를 개선한다.

JSP를 이용할 것이며, service 로직 호출 등은 모두 JSP 내에서 처리할 것이다. (절대 좋은 방식이 아니지만, 의도적으로 일단 그렇게 사용한다.)

# IndexServlet 변경

```java
package com.lickthespring.web.servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/index.jsp");
        dispatcher.forward(req, resp);
    }

}
```

JSP파일은 Servlet을 거치지 않고 바로 톰캣에 정적 파일 처럼 넣어도 정상적으로 실행이 가능하다.

톰캣이 Jasper라는 JSP 엔진을 이용해 JSP를 Servlet으로 변환해주기 때문이다.

그러나, 일단 기존 Servlet에서 JSP로(정확히는 JSP와 Jasper에 의해 생성된 Servlet으로) 작업을 위임해주는 형식으로 코드를 작성했다.

# CommentServlet

```java
package com.lickthespring.web.servlet;

import java.io.IOException;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.service.CommentService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/comments", loadOnStartup = 1)
public class CommentServlet extends HttpServlet {

    private final CommentService commentService = CommentService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/comments.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Comment comment = new Comment(req.getParameter("name"), req.getParameter("comment"));

        commentService.save(comment);

        resp.sendRedirect("/comments");
    }

}
```

# CommentViewServlet

```java
package com.lickthespring.web.servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/comments/*")
public class CommentViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] path = req.getRequestURL().toString().split("/");
        int id = Integer.parseInt(path[path.length - 1]);

        req.setAttribute("id", id);
    
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/comment.jsp");
        dispatcher.forward(req, resp);
    }

}
```

HttpServletRequest 객체에 setAttribute 메서드를 통해 값을 주입했다.

이 값은 JSP에서(혹은 작업을 위임받은 다른 서블릿에서) getAttribute 메서드를 통해 Object 타입으로 가져올 수 있다.

# pom.xml 변경

```java
<plugin>
	<artifactId>maven-war-plugin</artifactId>
	<version>3.3.2</version>
  <configuration>
    <failOnMissingWebXml>false</failOnMissingWebXml>
    <warSourceDirectory>${basedir}/src/main/webapp</warSourceDirectory>
    <webXml>${basedir}/src/main/webapp/WEB-INF/web.xml</webXml>
    <webResources>
      <webResource>
        <directory>${basedir}/src/main/webapp</directory>
      </webResource>
    </webResources>
  </configuration>
</plugin>
```

pom.xml의 maven-war-plugin 설정을 변경해, /src/main/webapp 디렉토리 전체가 war 파일 내부로 복사되도록 설정했다.

Servlet에서 작성한 바와 같이 jsp 파일은 /src/main/webapp/WEB-INF/views 경로에 넣으면 된다.

# index.jsp

```java
<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>메인</title>
    </head>
    <body>
        <h1>Index</h1>
        <a href="/comments">방명록 모두보기</a>

        <form method="post" action="/comments">
            <input type="text" name="name" placeholder="Name">
            <br>
            <textarea name="comment" placeholder="Comment"></textarea>
            <br>
            <button type="submit">Submit</button>
        </form>
    </body>
</html>
```

페이지의 content type과 encoding을 설정하는 코드가 필요하다.

JSP는 <%와 %> 사이에 자바 코드를 넣어 실행이 가능하다.

# comments.jsp

```java
<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.lickthespring.web.entity.Comment" %>
<%@ page import="com.lickthespring.web.service.CommentService" %>
<%
    final CommentService commentService = CommentService.getInstance();
    List<Comment> comments = commentService.findAll();
%>
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
            int count = commentService.count();
            for (int id = 0; id < count; id++) {
                Comment comment = comments.get(id);
        %>
        <div style="border:1px solid #CCC; margin:5px 10px; padding:10px;">
            <div><%=(id+1)%> / <%=count%></div>
            <div style="font-weight:bold"><%=comment.getName()%></div>
            <div><%=comment.getComment()%></div>
        </div>
        <%
            }
        %>
    </body>
</html>
```

# comment.jsp

```java
<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.lickthespring.web.entity.Comment" %>
<%@ page import="com.lickthespring.web.service.CommentService" %>
<%
    final CommentService commentService = CommentService.getInstance();
    int id = (int) request.getAttribute("id");
%>
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
            try {
                Comment comment = commentService.findById(id - 1);
        %>
        <div style="border:1px solid #CCC; margin:5px 10px; padding:10px;">
            <div style="font-weight:bold"><%=comment.getName()%></div>
            <div><%=comment.getComment()%></div>
        </div>
        <%
            } catch (IllegalArgumentException e) {
                out.print("존재하지 않는 방명록입니다.");
                return;
            }
        %>
</html>
```

CommentViewServlet에서 전달받은 값을 getAttribute 메서드로 가져왔다.

JSP 내부에서는 request, response 객체를 바로 사용할 수 있다.

# Jasper에 의해 변환된 Servlet 코드

톰캣은 JSP를 Jasper 엔진을 통해 Servlet으로 변환한다.

변환된 코드는 톰캣 경로의 work 폴더에서 확인할 수 있다.

work/Catalina/localhost/ROOT/org/apache/jsp/~~~~/views

```java
out.write("        <h1>전체 방명록</h1>\r\n");
out.write("        <div style=\"margin-bottom: 10px;\"><a href=\"/\">메인으로</a></div>\r\n");
out.write("\r\n");
out.write("        ");

      int count = commentService.count();
      for (int id = 0; id < count; id++) {
          Comment comment = comments.get(id);
  
out.write("\r\n");
out.write("        <div style=\"border:1px solid #CCC; margin:5px 10px; padding:10px;\">\r\n");
out.write("            <div>");
out.print((id+1));
out.write(' ');
out.write('/');
out.write(' ');
out.print(count);
out.write("</div>\r\n");
out.write("            <div style=\"font-weight:bold\">");
out.print(comment.getName());
out.write("</div>\r\n");
out.write("            <div>");
out.print(comment.getComment());
out.write("</div>\r\n");
out.write("        </div>\r\n");
```

코드 중 일부만 발췌하였다.

# 여담

약 십 년 전, PHP로 개발하며 만들었던 스파게티 코드로 만들어진 서비스를 운영하며 유지보수 했던 추억이 떠오른다…

상용 프레임워크도 사용하지 않고 자체적으로 MVC 패턴 프레임워크를 만들어 사용했었는데..

그 때로 다시 돌아가고 싶다. 

# 코드

지금까지 구현한 코드는 아래 레포지토리에서 볼 수 있다.

[https%3A%2F%2Fgithub.com%2Fyouhogeon%2FlickTheSpring%2Ftree%2F4e1d121a6a25f0b335bef22b413b2f1be1efa1fe%2Fweb](https://github.com/youhogeon/lickTheSpring/tree/4e1d121a6a25f0b335bef22b413b2f1be1efa1fe/web)