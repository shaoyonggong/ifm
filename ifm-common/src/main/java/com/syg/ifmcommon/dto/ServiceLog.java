package com.syg.ifmcommon.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceLog {

    public ServiceLog(){
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip=addr.getHostAddress();
            this.serviceIp=ip;
        } catch (UnknownHostException e) {}
    }

    public enum status_enum {
        NEW,
        REFUSED,
        FAILED,
        DONE
    }

    /** 唯一id */
    private String id;
    /** 状态 */
    private status_enum state;
    /** 参数 */
    private String param;
    /**  */
    private Long startTime;
    /** 结束时间 */
    private Long endTime;
    /** 地址 */
    private String path;
    /** 异常类型 */
    private String exType;
    /** 异常内容 */
    private String exMsg;
    /** 客户ip */
    private String clientIp;
    /** 服务器ip */
    private String serviceIp;
    /**  服务名称 */
    private String serverName;
    /** 重试次数 */
    private Integer retryCount;
    /**请求类型*/
    private String requestType;
    /**轨迹*/
    private String stacks;
    /**环境*/
    private String appEnv;
    /**链路Id*/
    private String tracingId;
    /**记录编号*/
    private Integer recordNumber;
    /**是否异步*/
    private String isSync;
}
