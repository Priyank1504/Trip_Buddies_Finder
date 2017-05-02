package com.example.priya.hw09;

/**
 * Created by Priyank Verma
 */

public class Comment {
    String id;
    String comment;
    String username;
    String userEmail;
    String time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return id != null ? id.equals(comment.id) : comment.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
