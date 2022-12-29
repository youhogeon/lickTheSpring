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
