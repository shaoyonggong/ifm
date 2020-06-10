package com.syg.ifmapi.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class PsnInfoPo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String account;

    private String uesrCode;

    private String userName;

    private String userPassward;

    private String email;

    private Long phone;

    private String deptCode;

    private String superiorCode;

    private Integer state;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

}
