package com.syg.ifmacl.interceptor;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public enum IFMDataPermissionMod {
    None(0b000000),
    Role_Read(0b010000),
    Role_Write(0b100000),
    Role_Own(0b110000),
    UpperGroup_Read(0b000100),
    UpperGroup_Write(0b001000),
    UpperGroup_Own(0b001100),
    SameGroup_Read(0b000001),
    SamerGroup_Write(0b000010),
    SameGroup_Own(0b000011);
    private int mod;

    private IFMDataPermissionMod(int mod) {
        this.mod = mod;
    }

    public int getMod() {
        return mod;
    }
}
