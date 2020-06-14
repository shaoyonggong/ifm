package com.syg.ifmcommon.result;

import com.syg.ifmcommon.dto.IFMResultErrorInfo;
import com.syg.ifmcommon.dto.vaildate.IFMVaildateFieldError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description IFM统一返回对象
 * @Author shaoyonggong
 * @Dae 2020/6/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IFMResult<D> implements Serializable {

    public void merge(IFMResult<D> ifmResult) {
        this.status = ifmResult.status;
        this.code = ifmResult.code;
        this.body = ifmResult.body;
        this.validationErrors.addAll(ifmResult.validationErrors);
        this.exception = ifmResult.exception;
        this.totalCount = ifmResult.totalCount;
    }

    public enum status_enum {
        SUCCESS,
        REFUSED,
        FAILED
    }

    /**
     * 状态码 成功201 拒绝203 失败205
     */
    public static final Integer SUCCESS_CODE = 201;
    public static final Integer REFUSED_CODE = 203;
    public static final Integer FAILED_CODE = 205;

    /**
     * 状态码
     */
    private Integer code = SUCCESS_CODE;

    /**
     * 状态
     */
    private status_enum status = status_enum.SUCCESS;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 异常
     */
    private IFMResultErrorInfo exception = null;

    /**
     * 主体
     */
    private D body;

    /**
     * 验证错误
     */
    private List<IFMVaildateFieldError> validationErrors = new ArrayList<>();

    /**
     * 总数
     */
    private int totalCount;

    /**
     * 原始参数
     */
    private Object originalParam;

    /**
     * 标记服务拒绝处理(由于验证失败等原因.)
     */
    public void markSuccess() {
        this.code = SUCCESS_CODE;
        this.status = status_enum.SUCCESS;
    }

    /**
     * 标记服务拒绝处理(由于验证失败等原因.)
     */
    public void markRefused() {
        this.code = REFUSED_CODE;
        this.status = status_enum.REFUSED;
    }

    /**
     * 标记服务的处理结果发生了异常.
     */
    public void markFailed() {
        this.code = FAILED_CODE;
        this.status = status_enum.FAILED;

    }

}
