package com.syg.ifmapi.constant;

import lombok.Data;

/**
 * @Description 状态常量
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public enum StatusEnum {

    SUCCESS_CODE(201,"成功"),
    REFUSE_CODE(203,"拒绝"),
    FAIL_CODE (205,"失败");

    private Integer code;

    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    StatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static StatusEnum parse(String code) {
        if(code==null){
            return null;
        }
        for (StatusEnum statusType : StatusEnum.values()) {
            if (statusType.getCode().equals(code)) {
                return statusType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "StatusEnum{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
