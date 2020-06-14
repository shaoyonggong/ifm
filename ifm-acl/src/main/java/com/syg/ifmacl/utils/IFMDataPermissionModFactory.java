package com.syg.ifmacl.utils;

/**
 * @Description 此类为permissionMod辅助类, 此列举了一些常用的权限认定码，如需额外可参照配置
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public class IFMDataPermissionModFactory {

    public static final int ROLE_READ = 0b010000;
    public static final int ROLE_WRITE = 0b100000;
    public static final int UPPERGROUP_READ = 0b000100;
    public static final int UPPERGROUP_WRITE = 0b001000;
    public static final int SAMEGROUP_READ = 0b000001;
    public static final int SAMEGROUP_WRITE = 0b000010;
    public static final int ROLE_UPPERGROUP_SAMEGROUP_READ = 0b010101;
    public static final int ROLE_UPPERGROUP_SAMEGROUP_WRITE = 0b101010;
}
