package com.syg.ifmcommon.constant;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GlobalConstant {

    /**
     * 状态码 成功201 拒绝203 失败205
     */
    public static final int SUCCESS_CODE = 201;
    public static final int REFUSED_CODE = 203;
    public static final int FAILED_CODE = 205;
}
