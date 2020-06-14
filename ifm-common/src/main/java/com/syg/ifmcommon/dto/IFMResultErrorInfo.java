package com.syg.ifmcommon.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IFMResultErrorInfo {

    public IFMResultErrorInfo() {
    }

    public IFMResultErrorInfo(String code, String message) {
        super();
        this.errorCode = code;
        this.message = message;
    }

    public IFMResultErrorInfo(String code, String message, String errorStack) {
        this.errorCode = code;
        this.errorStack = errorStack;
        this.message = message;
    }

    public IFMResultErrorInfo(String code, String message, List<String> messageStacks) {
        super();
        this.errorCode = code;
        this.message = message;
        if (messageStacks != null) {
            this.messageStacks = messageStacks;
        }
        this.messageStacks.add(0, message);
    }

    /**
     * 异常编码
     */
    private String errorCode;

    /**
     * 未知错误
     */
    private String message = null;

    /**
     * 异常堆栈
     */
    private String errorStack = null;

    /**
     * 跟踪异常信息
     */
    private List<String> messageStacks = null;
}
