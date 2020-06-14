package com.syg.ifmcommon.dto.vaildate;

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
public class IFMVaildateFieldError {

    public IFMVaildateFieldError() {
    }

    public IFMVaildateFieldError(String fieldName, String errorMessage, Object errorValue) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
        this.errorValue = errorValue;
    }

    public IFMVaildateFieldError(String fieldName, String errorMessage, Object errorValue, Integer index) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
        this.errorValue = errorValue;
        this.index = index;
    }

    /**
     * 下标
     */
    private Integer index;

    /**
     * 对象名
     */
    private String fieldName;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 验证不通过的原始值
     */
    private Object errorValue = null;

    private List<IFMVaildateFieldError> ifmVaildateFieldErrors;
}

