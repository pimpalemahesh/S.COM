package com.myinnovation.socom.Model;

public class Request {
    String requestId, requestStatus, requestBy, requestTo, requestAt;
    boolean isblocked;

    public Request(String requestId, String requestStatus, String requestBy, String requestAt) {
        this.requestId = requestId;
        this.requestStatus = requestStatus;
        this.requestBy = requestBy;
        this.requestAt = requestAt;
    }

    public Request() {
    }

    public String getRequestTo() {
        return requestTo;
    }

    public void setRequestTo(String requestTo) {
        this.requestTo = requestTo;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public String getRequestAt() {
        return requestAt;
    }

    public void setRequestAt(String requestAt) {
        this.requestAt = requestAt;
    }

    public boolean isIsblocked() {
        return isblocked;
    }

    public void setIsblocked(boolean isblocked) {
        this.isblocked = isblocked;
    }
}
