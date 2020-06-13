package com.syg.ifmapi.handler;

import com.syg.ifmapi.result.IFMResult;
import com.syg.ifmapi.result.IFMServerRejectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description 异常处理类
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public IFMResult serverError(Exception e) {
        String errMsg = "";
        if (e instanceof NullPointerException) {
            errMsg = "发生空指针异常";
        } else if (e instanceof RuntimeException) {
            errMsg = "发生运行时异常";
        } else {
            errMsg = "发生未知异常";
        }
        log.error("############" + errMsg + "############", e);
        return IFMServerRejectException.serverErrException(errMsg);
    }

    @ExceptionHandler(value = IFMServerRejectException.class)
    public IFMResult<Object> paramError(IFMServerRejectException e) {
        log.info("############" + e.getMsg() + "############");
        return IFMServerRejectException.serverErrException("",e);
    }
}
