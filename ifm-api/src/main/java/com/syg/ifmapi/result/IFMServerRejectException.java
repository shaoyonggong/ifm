package com.syg.ifmapi.result;

import com.alibaba.druid.support.json.JSONUtils;

/**
 * @Description 运行时异常类
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class IFMServerRejectException extends RuntimeException{

    private static final long serialVersionUID = 4453214753962022203L;

    private Integer code;
    private String msg;

    public IFMServerRejectException(){

    }
    public IFMServerRejectException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public IFMServerRejectException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public IFMServerRejectException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public IFMServerRejectException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public static IFMResult serverErrException(String message) {
        IFMResult ifmResult = new IFMResult();
        ifmResult.markRefuse();
        ifmResult.setMessage(message);
        return ifmResult;
    }

    public static IFMResult serverErrException(String message, Exception e) {
        IFMResult ifmResult = new IFMResult();
        ifmResult.markRefuse();
        ifmResult.setMessage(message);
        ifmResult.setException(JSONUtils.toJSONString(e));
        return ifmResult;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
