package com.lickthespring.web;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// package com.lickthespring.web;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// public class SampleController {
    
//     @GetMapping("/test")
//     public String hello() {
//         System.out.println("hello()");

//         return "Hello World2!";
//     }

// }

@WebServlet(value = "/*", loadOnStartup = -1) 
public class SampleController extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("Init Servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        System.out.println("Got Request from " + requestURI);

        PrintWriter writer = resp.getWriter();
        resp.addHeader("content-type", "application/json");
        resp.setStatus(400);
        writer.println("Hello Servlet3!");
    }

}
