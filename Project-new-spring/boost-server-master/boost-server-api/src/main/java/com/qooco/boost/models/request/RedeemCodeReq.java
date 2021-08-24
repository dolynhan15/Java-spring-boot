package com.qooco.boost.models.request;

public class RedeemCodeReq {
    private String code;

    public RedeemCodeReq() {
    }

    public RedeemCodeReq(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
