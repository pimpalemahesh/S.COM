package com.myinnovation.socom.Model;

public class Request {
    String requestId, requestTo;
    long requestAt;

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

    public long getRequestAt() {
        return requestAt;
    }

    public void setRequestAt(long requestAt) {
        this.requestAt = requestAt;
    }

}
