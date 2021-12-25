package com.myinnovation.socom.Model;

public class Post {
    private String postId;
    private String postImage;
    private String postBy;
    private String postDescription;
    private long postAt;
    private int postLike;

    public Post() {
    }

    public Post(String postId, String postImage, String postBy, String postDescription, long postAt) {
        this.postId = postId;
        this.postImage = postImage;
        this.postBy = postBy;
        this.postDescription = postDescription;
        this.postAt = postAt;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public long getPostAt() {
        return postAt;
    }

    public void setPostAt(long postAt) {
        this.postAt = postAt;
    }
}
