package com.lickthespring.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@WebServlet(value = "/*")
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
