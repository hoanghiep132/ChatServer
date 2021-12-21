package com.hiepnh.chatserver.model.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseResponse {

    protected int code;
    protected String message;

    public BaseResponse() {

    }

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setSuccess(String msg) {
        this.code = 0;
        this.message = msg;
    }

    public void setSuccess() {
        this.setSuccess("OK");
    }

    public void setResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String baseInfo() {
        return code + "|" + message;
    }

}
