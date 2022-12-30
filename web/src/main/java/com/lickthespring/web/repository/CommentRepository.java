package com.lickthespring.web.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lickthespring.web.entity.Comment;

@Repository
public class CommentRepository {
    
    private final static List<Comment> comments = new ArrayList<>();

    public Comment findById(int id) {
        try {
            return comments.get(id);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public List<Comment> findAll() {
        return comments;
    }

    public int count() {
        return comments.size();
    }

    public void save(Comment comment) {
        comments.add(comment);
    }

}
