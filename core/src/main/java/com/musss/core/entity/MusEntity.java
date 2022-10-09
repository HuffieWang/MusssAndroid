package com.musss.core.entity;

public class MusEntity {
    public int code = 200;
    public String msg = "Success";

    public MusEntity() {
    }

    public MusEntity(String msg) {
        this.msg = msg;
    }

    public MusEntity(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
