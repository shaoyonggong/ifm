package com.syg.rouyi.project.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description 部门信息
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Dept implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 部门ID */
    private String deptId;
    /** 父部门ID */
    private String parentId;
    /** 祖级列表 */
    private String ancestors;
    /** 部门名称 */
    private String deptName;
    /** 祖级列表 */
    private String ancestorsReal;
    /** 部门名称 */
    private String deptCode;
    /** 显示顺序 */
    private String orderNum;
    /** 负责人 */
    private String leader;
    /** 联系电话 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** 部门状态:0正常,1停用 */
    private String status;
    /** 父部门名称 */
    private String parentName;

}
