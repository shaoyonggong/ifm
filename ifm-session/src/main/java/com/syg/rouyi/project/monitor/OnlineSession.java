package com.syg.rouyi.project.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.session.mgt.SimpleSession;

import java.io.Serializable;

/**
 * @Description 在线用户会话属性
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OnlineSession extends SimpleSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名称 */
    private String loginName;

    /** 部门名称 */
    private String deptName;

    /** 登录IP地址 */
    private String host;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 在线状态 */
    private OnlineStatus status = OnlineStatus.on_line;

    /** 属性是否改变 优化session数据同步 */
    private transient boolean attributeChanged = false;

    @Override
    public String getHost()
    {
        return host;
    }

    @Override
    public void setHost(String host)
    {
        this.host = host;
    }

    @Override
    public void setAttribute(Object key, Object value)
    {
        super.setAttribute(key, value);
    }

    @Override
    public Object removeAttribute(Object key)
    {
        return super.removeAttribute(key);
    }

    public static enum OnlineStatus
    {
        /** 用户状态 */
        on_line("在线"), off_line("离线");
        private final String info;

        private OnlineStatus(String info)
        {
            this.info = info;
        }

        public String getInfo()
        {
            return info;
        }
    }
}
