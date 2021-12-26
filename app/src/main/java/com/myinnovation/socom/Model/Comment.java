package com.myinnovation.socom.Model;

public class Comment {
    private String commentBody;
    private long commentAt;
    private String commentedBy;

    public Comment(String commentBody, long commentAt, String commentedBy) {
        this.commentBody = commentBody;
        this.commentAt = commentAt;
        this.commentedBy = commentedBy;
    }

    public Comment() {
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public long getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(long commentAt) {
        this.commentAt = commentAt;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }
}
