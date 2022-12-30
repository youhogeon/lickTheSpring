package com.lickthespring.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lickthespring.web.entity.Comment;
import com.lickthespring.web.repository.CommentRepository;

@Service
public class CommentService {
    
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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
