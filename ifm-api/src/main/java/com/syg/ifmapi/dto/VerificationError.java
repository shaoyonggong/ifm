package com.syg.ifmapi.dto;

import lombok.Data;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Data
public class VerificationError {

    /**
     * 状态码
     */
    private final Integer SUCCESS_CODE = 201;
    private final Integer REFUSE_CODE = 203;
    private final Integer FAIL_CODE = 205;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 下标
     */
    private Integer index;

    /**
     * 对象名
     */
    private String propertyName;

    /**
     * 错误描述
     */
    private String errorDescription;
}

