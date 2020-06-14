package com.syg.idempotent;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public enum ValidateType {

    //走messageID，如果messageId不存在走param
    MESSAGEID("messageId"),
    //直接走param
    PARAM("param");

    String type;

    public String getType() {
        return type;
    }
    ValidateType(String type) {
        this.type = type;
    }
}
