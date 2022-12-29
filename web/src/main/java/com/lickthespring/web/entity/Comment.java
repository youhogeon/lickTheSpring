package com.lickthespring.web.entity;

public class Comment {
    
    private final String name;
    private final String comment;

    public Comment(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

}
