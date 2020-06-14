package com.syg.ifmsession.session;

import com.syg.ifmsession.RedisSessionDAO;
import com.syg.ifmsession.po.UserPo;
import com.syg.rouyi.project.system.User;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Component
public class IFMSessionUtil {

    @Autowired(required = false)
    RedisSessionDAO redisSessionDAO;

    private static IFMSessionUtil ifmSessionUtil;

    protected static Logger logger = LoggerFactory.getLogger(IFMSessionUtil.class);

    public static User getUser() throws Exception {

        if (ifmSessionUtil.redisSessionDAO == null) {
            logger.warn("请检查配置文件中是否添加有关session源的redis配置文件");
            throw new Exception("请检查配置文件中是否添加有关session源的redis配置文件");
        }

        if (MDC.get("sessionId") == null) {
            logger.warn("当前无sessionId");
            return null;
        }
        //根据sessionId获取对应redis存储的session对象
        Session session = ifmSessionUtil.redisSessionDAO.readSession(MDC.get("sessionId"));
        if (session == null) {
            logger.warn("当前无关于该sessionId的session信息！id=" + MDC.get("sessionId"));
            return null;
        }
        try {
            Collection<Object> attributeKeys = session.getAttributeKeys();
            Object[] objects = attributeKeys.toArray();
            User user = null;
            for (Object object : objects) {
                Object attribute = session.getAttribute(object);
                if (attribute instanceof SimplePrincipalCollection) {
                    Object result = ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
                    user = (User) result;
                }
            }
            if (user != null) {
                ifmSessionUtil.redisSessionDAO.flushTtl(MDC.get("sessionId"));
            }
            return user;
        } catch (Exception e) {
            logger.warn("在获取session信息时出错！错误信息：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @PostConstruct
    public void init() {
        ifmSessionUtil = this;
    }

    public static String getUserName() throws Exception {
        User user = getUser();
        return user == null ? null : user.getUserName();
    }

    public static String getSessionId() {
        return MDC.get("sessionId");
    }

    public static UserPo getUserPo() throws Exception {
        User user = getUser();
        if (user == null) {
            return null;
        } else {
            UserPo userPo = new UserPo();
            userPo.setOwner(user.getLoginName());
            if (user.getDept() != null && !StringUtils.isEmpty(user.getDept().getAncestors())) {
                userPo.setOwnerGroup(user.getDept().getAncestors());
            } else {
                userPo.setOwnerGroup(null);
            }
            return userPo;
        }
    }
}
