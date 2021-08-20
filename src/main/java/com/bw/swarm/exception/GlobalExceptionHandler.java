package com.bw.swarm.exception;

import com.bw.swarm.constants.enums.ResultCode;
import com.bw.swarm.dto.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 全局异常处理类
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestResponse APIExceptionHandler(Exception e) {
        log.error("接口异常： ", e);
        return RestResponse.error(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMsg());
    }

    /**
     * 自定义异常APIException
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public RestResponse BizExceptionHandler(BizException e) {
        return RestResponse.error(e.getCode(), e.getErrorMsg());
    }
}
