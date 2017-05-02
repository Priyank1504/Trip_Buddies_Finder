package com.example.priya.hw09;

import java.util.ArrayList;

/**
 * Created by priyank Verma
 */

public class MessageDetail {
    public String Id, UserId, Comment, FileThumbnailId, Type, CreatedAt, UpdatedAt, post, username;

    ArrayList<com.example.priya.hw09.Comment> postcomments;

    @Override
    public String toString() {
        return "MessageDetail{" +
                "Id='" + Id + '\'' +
                ", UserId='" + UserId + '\'' +
                ", Comment='" + Comment + '\'' +
                ", FileThumbnailId='" + FileThumbnailId + '\'' +
                ", Type='" + Type + '\'' +
                ", CreatedAt='" + CreatedAt + '\'' +
                ", UpdatedAt='" + UpdatedAt + '\'' +
                ", post='" + post + '\'' +
                ", username='" + username + '\'' +
                ", postcomments=" + postcomments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageDetail that = (MessageDetail) o;

        if (!Id.equals(that.Id)) return false;
        if (!UserId.equals(that.UserId)) return false;
        return CreatedAt.equals(that.CreatedAt);

    }

    public MessageDetail() {
        this.postcomments=new ArrayList<>();
    }

    public MessageDetail(ArrayList<com.example.priya.hw09.Comment> postcomments) {
        this.postcomments = postcomments;
    }

    @Override
    public int hashCode() {
        int result = Id.hashCode();
        result = 31 * result + UserId.hashCode();
        result = 31 * result + CreatedAt.hashCode();
        return result;
    }

}
