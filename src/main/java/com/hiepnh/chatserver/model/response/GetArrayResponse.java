package com.hiepnh.chatserver.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GetArrayResponse<T> extends BaseResponse {

    private long total;
    private List<T> rows;

    public GetArrayResponse() {
        super();
        this.total = 0;
        this.rows = new ArrayList<>();
    }

    public String info() {
        return "code = " + code + ", message = " + message + ", size = " + (rows != null ? rows.size() : 0) + ", total = " + total;
    }

    @Override
    public String baseInfo() {
        return this.info();
    }

}
