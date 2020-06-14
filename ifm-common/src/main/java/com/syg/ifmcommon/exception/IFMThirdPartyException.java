package com.syg.ifmcommon.exception;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description 第三方平台异常
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class IFMThirdPartyException extends IFMException {

    /**
     * 第三方平台名称，通知客户端第三方物流的名称。
     */
    @ApiModelProperty(value = "第三方平台名称", name = "第三方平台名称", example = "德邦物流")
    private String thirdPartyName = null;

    public IFMThirdPartyException(String code, String message, String thirdPartyName, Throwable t) {
        super(code, message, t);
        this.thirdPartyName = thirdPartyName;
    }

}
