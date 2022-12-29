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
