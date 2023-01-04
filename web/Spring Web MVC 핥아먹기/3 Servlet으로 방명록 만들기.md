# 3. Servlet으로 방명록 만들기

# 개요

Servlet을 이용해 방명록을 만들어 본다.

이후 템플릿 엔진을 이용해 코드를 개선하고 최종적으로 Spring MVC를 이용해 개선할 것이다.

# Comment Entity

```java
package com.lickthespring.web.entity;

public class Comment {
    
    private final String name;
    private final String comment;

    public Comment(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

}
```

# CommentRepository

```java
package com.lickthespring.web.repository;

import java.util.ArrayList;
import java.util.List;

import com.lickthespring.web.entity.Comment;

public class CommentRepository {
    
    private final static CommentRepository instance = new CommentRepository();
    private final static List<Comment> comments = new ArrayList<>();

    private CommentRepository() { }

    public static CommentRepository getInstance() {
        return instance;
    }

    public Comment findById(int id) {
        try {
            return comments.get(id);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public List<Comment> findAll() {
        return comments;
    }

    public int count() {
        return comments.size();
    }

    public void save(Comment comment) {
        comments.add(comment);
    }

}
```

여러 Servlet에서 CommentRepository 객체를 계속 만들면 비효율적이므로, 싱글톤 패턴을 사용함.

# CommentService

```java
package com.lickthespring.web.service;

import java.util.List;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.repository.CommentRepository;

public class CommentService {
    
    private final static CommentService instance = new CommentService();
    private final CommentRepository commentRepository = CommentRepository.getInstance();

    private CommentService() { }

    public static CommentService getInstance() {
        return instance;
    }

    public Comment findById(int id) {
        Comment comment = commentRepository.findById(id);

        if (comment == null) {
            throw new IllegalArgumentException("방명록이 존재하지 않습니다.");
        }

        return comment;
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public int count() {
        return commentRepository.count();
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

}
```

# IndexServlet

메인 페이지 (방명록 입력 폼)

```java
package com.lickthespring.web.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html");

        resp.getWriter().write("<!doctype html><html><head><meta charset=\"UTF-8\"><title>메인</title></head><body><h1>Index</h1><a href=\"/comments\">방명록 모두보기</a><form method=\"post\" action=\"/comments\"><input type=\"text\" name=\"name\" placeholder=\"Name\"><br><textarea name=\"comment\" placeholder=\"Comment\"></textarea><br><button type=\"submit\">Submit</button></form></body></html>");
    }

}
```

content type과 character set을 응답 헤더에 넣어야 한다.

더불어 URL 패턴에 “/”를 입력하면 모든 요청(다른 서블릿에서 등록한 URL 패턴 제외)을 IndexServlet이 받게 된다.

우리는 도메인 루트만을 등록하고 싶었던 것이지, localhost:8080/asdflkjfoiwfj23o 과 같은 모든 페이지를 연결하고자 한 것이 아니다.

URL 패턴을 빈 값으로 두면 오로지 루트만을 연결해준다.

# CommentServlet

전체 방명록 조회(GET /comments) 와 작성(POST /comments) 을 처리

```java
package com.lickthespring.web.servlet;

import java.io.IOException;
import java.util.List;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.service.CommentService;

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
        List<Comment> comments = commentService.findAll();

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html");
        
        resp.getWriter().write("<!doctype html><html><head><meta charset=\"UTF-8\"><title>전체 방명록</title></head><body><h1>전체 방명록</h1>");
        resp.getWriter().write("<div style=\"margin-bottom: 10px;\"><a href=\"/\">메인으로</a></div>");

        int count = commentService.count();
        for (int id = 0; id < count; id++) {
            Comment comment = comments.get(id);

            resp.getWriter().write("<div style=\"border:1px solid #CCC; margin:5px 10px; padding:10px;\">");
            resp.getWriter().write("<div>" + (id + 1) + " / " + count + "</div>");
            resp.getWriter().write("<div style=\"font-weight: bold;\">" + comment.getName() + "</div>");
            resp.getWriter().write("<div>" + comment.getComment() + "</div>");
            resp.getWriter().write("</div>");
        }

        resp.getWriter().write("</body></html>");
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

개별 방명록 조회(GET /comments/{id}) 처리

```java
package com.lickthespring.web.servlet;

import java.io.IOException;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.service.CommentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/comments/*")
public class CommentViewServlet extends HttpServlet {

    private final CommentService commentService = CommentService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] path = req.getPathInfo().split("/");
        int id = Integer.parseInt(path[path.length - 1]);

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html");

        resp.getWriter().write("<!doctype html><html><head><meta charset=\"UTF-8\"><title>방명록 상세보기</title></head><body><h1>방명록 상세보기</h1>");
        resp.getWriter().write("<div style=\"margin-bottom: 10px;\"><a href=\"/\">메인으로</a></div>");

        try{
            Comment comment = commentService.findById(id - 1);

            resp.getWriter().write("<div style=\"border:1px solid #CCC; margin:5px 10px; padding:10px;\">");
            resp.getWriter().write("<div>" + id + "</div>");
            resp.getWriter().write("<div style=\"font-weight: bold;\">" + comment.getName() + "</div>");
            resp.getWriter().write("<div>" + comment.getComment() + "</div>");
            resp.getWriter().write("</div></body></html>");
        } catch (IllegalArgumentException e) {
            resp.setStatus(404);
            resp.getWriter().write(e.getMessage() + "</body></html>");

            return;
        }
    }

}
```

표준 Servlet에는 /comments/{id} 와 같이 URL parameter를 처리할 수 있는 기능이 없으므로, path info를 가져와 문자열 처리를 통해 {id} 값을 얻음

# 결론

중복되는 코드가 굉장히 많고, Java 코드 내에 HTML 코드가 난잡하게 섞여 있는 문제가 있음.

# 코드

지금까지 구현한 코드는 아래 레포지토리에서 볼 수 있다.

[https%3A%2F%2Fgithub.com%2Fyouhogeon%2FlickTheSpring%2Ftree%2F369e582fae385ed503e0086076bddc33aa1c9237%2Fweb](https://github.com/youhogeon/lickTheSpring/tree/369e582fae385ed503e0086076bddc33aa1c9237/web)