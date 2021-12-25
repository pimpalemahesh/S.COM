package com.myinnovation.socom.Model;

public class FollowModel {

    private String followedBy;
    private long followedAt;

    public FollowModel() {
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }

    public FollowModel(String followedBy, long followedAt) {
        this.followedBy = followedBy;
        this.followedAt = followedAt;
    }
}
