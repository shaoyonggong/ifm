package com.syg.rouyi.project.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 用户信息
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @ApiModelProperty(hidden = true)
    private Long userId;

    /** 部门ID */
    @ApiModelProperty(hidden = true)
    private String deptId;

    /** 部门父ID */
    @ApiModelProperty(hidden = true)
    private String parentId;

    /** 登录名称 */
    private String loginName;

    /** 用户名称 */
    @ApiModelProperty(hidden = true)
    private String userName;

    /** 用户邮箱 */
    @ApiModelProperty(hidden = true)
    private String email;

    /** 手机号码 */
    @ApiModelProperty(hidden = true)
    private String phonenumber;

    /** 用户性别 */
    @ApiModelProperty(hidden = true)
    private String sex;

    /** 用户头像 */
    @ApiModelProperty(hidden = true)
    private String avatar;

    /** 密码 */
    @ApiModelProperty(hidden = true)
    private String password;

    /** 盐加密 */
    @ApiModelProperty(hidden = true)
    private String salt;

    /** 帐号状态（0正常 1停用） */
    @ApiModelProperty(hidden = true)
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty(hidden = true)
    private String delFlag;

    /** 最后登陆IP */
    @ApiModelProperty(hidden = true)
    private String loginIp;

    /** 最后登陆时间 */
    @ApiModelProperty(hidden = true)
    private Date loginDate;

    /** 部门对象 */
    @ApiModelProperty(hidden = true)
    private Dept dept;

    /** 角色组 */
    @ApiModelProperty(hidden = true)
    private Long[] roleIds;

    /** 岗位组 */
    @ApiModelProperty(hidden = false)
    private Long[] postIds;

    /** 账户类型*/
    @ApiModelProperty(hidden = true)
    private String type;

    private String codes;

    /** 统一编码 */
    private String usercode;
    /** 公司名称 */
    private String unitname;
    /** 员工号 */
    private String clerkcode;
    /** 职务 */
    private String jobname;
    /** 部门编码 */
    private String deptcode;
    /** 部门名称 */
    private String deptname;
    /** 岗位名称 */
    private String postname;
    /** 企业号*/
    private String companycode;

    /**
     * 生成随机盐
     */
    public void randomSalt()
    {
        // 一个Byte占两个字节，此处生成的3字节，字符串长度为6
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        String hex = secureRandom.nextBytes(3).toHex();
        setSalt(hex);
    }
}
