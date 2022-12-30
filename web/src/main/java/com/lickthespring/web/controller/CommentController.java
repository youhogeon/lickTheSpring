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

    private final CommentService commentService;
    
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

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
