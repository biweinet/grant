package com.bw.swarm.exception;

import com.bw.swarm.constants.enums.ResultCode;
import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected int code;
    /**
     * 错误信息
     */
    protected String errorMsg;


    public BizException(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public BizException(String errorMsg) {
        this.code = ResultCode.FAILED.getCode();
        this.errorMsg = errorMsg;
    }
}

