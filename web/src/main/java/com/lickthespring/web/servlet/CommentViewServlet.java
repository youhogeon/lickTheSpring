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

        resp.getWriter().write("<!doctype html><html><head><meta charset=\"UTF-8\"><title>Index</title></head><body><h1>방명록 상세보기</h1>");
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
