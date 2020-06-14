package com.syg.ifmacl.support;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public enum Permission {

    OWN("o", "拥有者"),
    WRITE("w", "写"),
    READ("r", "读"),
    NONE("n", "无权限");

    Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
