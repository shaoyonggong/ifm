package com.syg.ifmserver.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.ComponentScan;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author shaoyonggong
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PsnInfo对象", description = "")
@TableName("psn_info")
@ComponentScan
public class PsnInfoPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Integer id;

    @TableField("account")
    private String account;

    @TableField("user_code")
    private String userCode;

    @TableField("user_name")
    private String userName;

    @TableField("user_passward")
    private String userPassward;

    @TableField("eamil")
    private String email;

    @TableField("phone")
    private Long phone;

    @TableField("dape_code")
    private String deptCode;

    @TableField("superior_code")
    private String superiorCode;

    @TableField("state")
    private Integer state;

    @TableField("create_date")
    private LocalDateTime createDate;

    @TableField("update_date")
    private LocalDateTime updateDate;

}
