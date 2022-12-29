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
        
        resp.getWriter().write("<!doctype html><html><head><meta charset=\"UTF-8\"><title>Index</title></head><body><h1>전체 방명록</h1>");
        resp.getWriter().write("<div style=\"margin-bottom: 10px;\"><a href=\"/\">메인으로</a></div>");

        int count = commentService.count();
        for (int id = 0; id < count; id++) {
            Comment comment = comments.get(id);

            resp.getWriter().write("<div style=\"border:1px solid #CCC; margin:5px 10px; padding:10px;\">");
            resp.getWriter().write("<div>" + (id + 1) + "</div>");
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
