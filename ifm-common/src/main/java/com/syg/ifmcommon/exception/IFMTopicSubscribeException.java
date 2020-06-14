package com.syg.ifmcommon.exception;


import com.syg.ifmcommon.constant.ExceptionConst;
import com.syg.ifmcommon.dto.IFMResultErrorInfo;
import com.syg.ifmcommon.dto.vaildate.ExceptionUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 服务拒绝异常
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IFMTopicSubscribeException extends Exception {

    public IFMTopicSubscribeException(String message) {
        super(message);
        this.errorCode = ExceptionConst.TOPIC_ERROR_CODE;
        this.message = message;
    }

    public IFMTopicSubscribeException(String code, String message) {
        super(message);
        this.errorCode = ExceptionConst.TOPIC_ERROR_CODE;
        this.message = message;
    }

    public IFMTopicSubscribeException(String code, String message, List<String> messageStacks) {
        super(message);
        this.errorCode = code;
        this.message = message;
        if (messageStacks != null) {
            this.messageStacks = messageStacks;
        }
        this.messageStacks.add(0, message);
    }

    public IFMTopicSubscribeException(String code, String message, Throwable t) {
        super(message, t);
        this.errorCode = ExceptionConst.TOPIC_ERROR_CODE;
        this.message = message;
        if (t != null) {
            this.errorStacks = ExceptionUtils.getStackTrace(t);
        }
    }

    public IFMTopicSubscribeException(IFMResultErrorInfo ifmResultErrorInfo) {
        super("");
        if (ifmResultErrorInfo.getMessageStacks() != null) {
            this.messageStacks = ifmResultErrorInfo.getMessageStacks();
        }
    }

    public IFMTopicSubscribeException(String message, IFMResultErrorInfo ifmResultErrorInfo) {
        super(message);
        if (ifmResultErrorInfo.getMessageStacks() != null) {
            this.messageStacks = ifmResultErrorInfo.getMessageStacks();
        }
    }

    public IFMTopicSubscribeException(String message, Throwable t) {
        super(message, t);
        this.errorCode = ExceptionConst.TOPIC_ERROR_CODE;
        this.message = message;
        if (t != null) {
            this.errorStacks = ExceptionUtils.getStackTrace(t);
        }
    }

    /**
     * 异常编码
     */
    @ApiModelProperty(value = "异常编码", name = "异常编码", example = "0")
    private String errorCode;

    /**
     * 未知错误
     */
    @ApiModelProperty(value = "简述", name = "简述", example = "订单下单的处理失败")
    private String message = null;

    /**
     * 异常堆栈
     */
    @ApiModelProperty(value = "异常堆栈", name = "异常堆栈", example = "")
    private String errorStacks = null;

    /**
     * 跟踪异常信息
     */
    @ApiModelProperty(value = "跟踪异常信息", name = "跟踪异常信息", example = "")
    private List<String> messageStacks = new ArrayList<>();
}
