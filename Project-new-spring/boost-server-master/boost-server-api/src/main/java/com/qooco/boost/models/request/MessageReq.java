package com.qooco.boost.models.request;

public class MessageReq {
    private String id;

    private int status;

    public MessageReq() {}

    public MessageReq(String id, int status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}