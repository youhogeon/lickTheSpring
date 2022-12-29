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
