package com.lickthespring.web.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/")
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html");

        resp.getWriter().write("<!doctype html><html><head><meta charset=\"UTF-8\"><title>메인</title></head><body><h1>Index</h1><a href=\"/comments\">방명록 모두보기</a><form method=\"post\" action=\"/comments\"><input type=\"text\" name=\"name\" placeholder=\"Name\"><br><textarea name=\"comment\" placeholder=\"Comment\"></textarea><br><button type=\"submit\">Submit</button></form></body></html>");
    }

}
