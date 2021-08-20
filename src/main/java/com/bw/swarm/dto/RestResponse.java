package com.bw.swarm.dto;

import com.bw.swarm.constants.enums.ResultCode;

import java.io.Serializable;

public class RestResponse<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public RestResponse() {
        this(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public RestResponse(int code, String msg) {
        this(code, msg, null);
    }

    public RestResponse(int code, String msg, T data) {
        code(code).msg(msg).result(data);
    }

    private RestResponse<T> code(int code) {
        setCode(code);
        return this;
    }

    private RestResponse<T> msg(String msg) {
        setMsg(msg);
        return this;
    }

    public RestResponse<T> result(T data) {
        setData(data);
        return this;
    }

    public static <T> RestResponse<T> ok(T t) {
        return new RestResponse(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), t);
    }

    public static <T> RestResponse<T> ok() {
        return new RestResponse();
    }

    public static <E> RestResponse<E> error(int code, String msg) {
        return new RestResponse(code, msg);
    }

    public static <T> RestResponse<T> error() {
        return new RestResponse(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMsg());
    }

    public static <T> RestResponse<T> error(String msg) {
        return new RestResponse(ResultCode.FAILED.getCode(), msg);
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    public static RestResponse toResponse(int rows) {
        return rows > 0 ? RestResponse.ok() : RestResponse.error();
    }
}
