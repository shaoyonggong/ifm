package com.syg.ifmacl.support;

import com.syg.ifmsession.po.UserPo;
import com.syg.ifmsession.session.IFMSessionUtil;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Component
public class SessionSupport {

    private String redisHost;

    public IFMDataPermissionSupport get() throws Exception {

        UserPo userPo = IFMSessionUtil.getUserPo();

        return new IFMDataPermissionSupport() {
            @Override
            public String getOwner() {
                return userPo == null ? null : userPo.getOwner();
            }

            @Override
            public void setOwner(String owner) {

            }

            @Override
            public String getOwnerGroup() {
                return userPo == null ? null : userPo.getOwnerGroup();
            }

            @Override
            public void setOwnerGroup(String ownerGroup) {

            }

            @Override
            public String getOwnerRole() {
                return userPo == null ? null : userPo.getOwnerRole();
            }

            @Override
            public void setOwnerRole(String ownerRole) {

            }

            @Override
            public Permission getPermission() {
                return null;
            }

            @Override
            public void setPermission(Permission permission) {

            }
        };
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }
}
