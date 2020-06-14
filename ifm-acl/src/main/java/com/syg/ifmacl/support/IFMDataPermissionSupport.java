package com.syg.ifmacl.support;

/**
 * @Description Entity需要实现的权限处理接口
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public interface IFMDataPermissionSupport {

    String getOwner();

    void setOwner(String owner);

    String getOwnerGroup();

    void setOwnerGroup(String ownerGroup);

    String getOwnerRole();

    void setOwnerRole(String ownerRole);

    Permission getPermission();

    void setPermission(Permission permission);
}
