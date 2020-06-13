package com.syg.ifmapi.result;

import com.syg.ifmapi.constant.GlobalConstant;
import com.syg.ifmapi.constant.StatusEnum;
import com.syg.ifmapi.dto.VerificationError;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description IFM统一返回对象
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Data
public class IFMResult<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 异常
     */
    private String exception = "";

    /**
     * 主体
     */
    private T body;

    /**
     * 验证错误
     */
    private List<VerificationError> errorList = new ArrayList<>();

    /**
     * 原始参数
     */
    private Object originalParam;

    /**
     * 标记成功
     */
    public void markSuccess(){
        this.code = GlobalConstant.SUCCESS_CODE;
        this.status = StatusEnum.SUCCESS_CODE;
    }

    /**
     * 标记拒绝
     */
    public void markRefuse(){
        this.code = GlobalConstant.REFUSE_CODE;
        this.status = StatusEnum.REFUSE_CODE;
    }

    /**
     * 标记失败
     */
    public void markFail(){
        this.code = GlobalConstant.FAIL_CODE;
        this.status = StatusEnum.FAIL_CODE;
    }

}
