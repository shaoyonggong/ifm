package com.syg.ifmacl.interceptor;

import com.syg.ifmacl.annotations.IFMPermissionMod;
import com.syg.ifmacl.annotations.IFMSensitiveField;
import com.syg.ifmacl.support.IFMDataPermissionSupport;
import com.syg.ifmacl.support.Permission;
import com.syg.ifmacl.support.SessionSupport;
import com.syg.ifmacl.utils.SessionUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description 查询结果的装饰器（此版本支持脱敏和装饰）
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
@Component
public class IFMAfterAuthenticationInterceptor implements Interceptor {

    @Autowired
    SessionSupport sessionSupport;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        if (!mappedStatement.getSqlCommandType().equals(SqlCommandType.SELECT)) {
            return invocation.proceed();
        }
        //此id是一个mapper具体到方法名的一个字符串
        String id = mappedStatement.getId();
        //查询如果没有结果集，不进行处理
        List list = (List) invocation.proceed();
        if (list == null || list.size() <= 0) {
            return list;
        }
        //对此查询做基本鉴权
        Boolean flag = IFMDataAuthorizeInterceptor.dealPermissionSuport(id, list.get(0));
        if (!flag) {
            return list;
        }
        //获取此查询的IFMPermissionMod的类名和具体的方法名
        String[] methodAndClassName = SessionUtils.spiltMethodAndClassName(id);
        Class<?> mapperClass = Class.forName(methodAndClassName[0]);
        //获取mapper类上，方法上的permissionmod值
        int permissionMod = 0;
        //是否允许脱敏
        boolean permissionModFlag = false;
        IFMPermissionMod anno = null;
        if (Objects.nonNull(anno = mapperClass.getAnnotation(IFMPermissionMod.class))) {
            permissionMod = anno.value();
            permissionModFlag = true;
        }
        //因为mybatis不支持mapper重载方法，所以用方法名字可以唯一确定一组方法
        Method method = null;
        IFMPermissionMod anno1 = null;
        if (Objects.nonNull(method = SessionUtils.findMethod(methodAndClassName[1], mapperClass)) && Objects.nonNull(anno1 = method.getAnnotation(IFMPermissionMod.class))) {
            permissionMod = anno1.value();
            permissionModFlag = true;
        }

        Map<Field, Class> fieldAndType = null;
        if (permissionModFlag) {
            //获取需要脱敏字段名字
            fieldAndType = getSensitiveFieldAndType(list.get(0));
        }
        IFMDataPermissionSupport s = null;
        if (Objects.nonNull(sessionSupport)) {
            s = sessionSupport.get();
        }
        //确定permission,根据permission进行脱敏
        for (Object o : list) {
            addPermissionAndRemoveSensitive(o, fieldAndType, permissionMod, s);
        }

        return list;
    }

    /**
     * 获取脱敏需要的Field和FieldType
     *
     * @param po
     * @return
     */
    private Map getSensitiveFieldAndType(Object po) {
        Map<Field, Class> fieldAndType = new HashMap();

        Field[] declaredFields = po.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            IFMSensitiveField fieldAnnotation = field.getAnnotation(IFMSensitiveField.class);
            Class<?> fieldType = field.getType();
            if (Objects.nonNull(fieldAnnotation)) {
                fieldAndType.put(field, fieldType);
            }
        }
        return fieldAndType;
    }

    /**
     * 执行填充permission值和脱敏（如果需要）
     *
     * @param o
     * @param fieldAndType
     * @param mod
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private void addPermissionAndRemoveSensitive(Object o, Map<Field, Class> fieldAndType, int mod, IFMDataPermissionSupport s) throws InstantiationException, IllegalAccessException {
        IFMDataPermissionSupport permissionSupport = (IFMDataPermissionSupport) o;
        //确定permission值
        Permission permission = null;
        if (s == null) {
            permission = Permission.NONE;
        } else {
            permission = determinePermission(permissionSupport, mod, s);
        }
        //设置Permission值
        permissionSupport.setPermission(permission);
        //如果权限不允许，则对敏感字段进行过滤(需要有敏感字段,且允许脱敏)
        if (permission.equals(Permission.NONE) && fieldAndType != null && fieldAndType.size() > 0) {
            Set<Map.Entry<Field, Class>> entries = fieldAndType.entrySet();
            for (Map.Entry<Field, Class> e : entries) {
                //脱敏
                removeSensitive(e.getKey(), e.getValue(), o);
            }
        }

    }

    /**
     * 更改敏感属性值
     *
     * @param field     属性
     * @param fieldType 属性类型
     * @param obj       需要更改敏感属性的对象
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void removeSensitive(Field field, Class fieldType, Object obj) throws IllegalAccessException, InstantiationException {
        field.setAccessible(true);
        if (fieldType.equals(String.class)) {
            field.set(obj, "******");
        } else if (fieldType.equals(BigDecimal.class)) {
            field.set(obj, new BigDecimal(0));
        } else if (fieldType.equals(int.class)) {
            field.setInt(obj, 0);
        } else if (fieldType.equals(Integer.class)) {
            field.set(obj, new Integer(0));
        } else if (fieldType.equals(long.class)) {
            field.setLong(obj, 0L);
        } else if (fieldType.equals(Long.class)) {
            field.set(obj, new Long(0L));
        } else if (fieldType.equals(float.class)) {
            field.setFloat(obj, 0f);
        } else if (field.equals(Float.class)) {
            field.set(obj, new Float(0f));
        } else if (fieldType.equals(double.class)) {
            field.setDouble(obj, 0d);
        } else if (field.equals(Double.class)) {
            field.set(obj, 0d);
        } else if (fieldType.equals(char.class)) {
            char c = 0;
            field.setChar(obj, c);
        } else if (fieldType.equals(Character.class)) {
            char c = 0;
            field.set(obj, new Character(c));
        } else if (fieldType.equals(boolean.class)) {
            field.setBoolean(obj, false);
        } else if (fieldType.equals(Boolean.class)) {
            field.set(obj, new Boolean(false));
        } else if (fieldType.equals(short.class)) {
            short s = 0;
            field.setShort(obj, s);
        } else if (fieldType.equals(Short.class)) {
            short s = 0;
            field.set(obj, new Short(s));
        } else if (fieldType.equals(byte.class)) {
            byte b = 0;
            field.setByte(obj, b);
        } else if (fieldType.equals(Byte.class)) {
            byte b = 0;
            field.set(obj, b);
        } else {
            field.set(field, null);
        }
    }

    /**
     * 确定Permission权限
     *
     * @param permissionSupport
     * @param mod
     * @return
     */
    private Permission determinePermission(IFMDataPermissionSupport permissionSupport, int mod, IFMDataPermissionSupport s) {
        if (s == null || permissionSupport == null) {
            return Permission.NONE;
        }
        //判定此session是否为数据的Owners
        if (!StringUtils.isEmpty(permissionSupport.getOwner()) && !StringUtils.isEmpty(s.getOwner()) && permissionSupport.getOwner().equals(s.getOwner())) {
            return Permission.OWN;
        }
        //用于全局只读权限的判定
        boolean flag = false;
        //判定此session是否为满足角色
        if (!StringUtils.isEmpty(permissionSupport.getOwnerRole()) && !StringUtils.isEmpty(s.getOwnerRole()) && SessionUtils.isContains(permissionSupport.getOwnerRole(), s.getOwnerRole())) {
            if ((mod & IFMDataPermissionMod.Role_Own.getMod()) >= IFMDataPermissionMod.Role_Write.getMod()) {
                return Permission.WRITE;
            } else if ((mod & IFMDataPermissionMod.Role_Own.getMod()) > 0) {
                flag = true;
            }
        }
        //判定此session是否满足上级
        if (!StringUtils.isEmpty(permissionSupport.getOwnerGroup()) && !StringUtils.isEmpty(s.getOwnerGroup())) {
            String[] sp = permissionSupport.getOwnerGroup().split(s.getOwnerGroup());
            if (sp.length > 1 && !StringUtils.isEmpty(sp[1])) {
                if ((mod & IFMDataPermissionMod.UpperGroup_Own.getMod()) >= IFMDataPermissionMod.UpperGroup_Write.getMod()) {
                    return Permission.WRITE;
                }
                if (!flag) {
                    if ((mod & IFMDataPermissionMod.UpperGroup_Own.getMod()) > 0) {
                        flag = true;
                    }
                }
            }
        }
        //判定session是否满足同僚
        if (!StringUtils.isEmpty(permissionSupport.getOwnerGroup()) && !StringUtils.isEmpty(s.getOwnerGroup()) && permissionSupport.getOwnerGroup().equals(s.getOwnerGroup())) {
            if ((mod & IFMDataPermissionMod.SameGroup_Own.getMod()) >= IFMDataPermissionMod.SamerGroup_Write.getMod()) {
                return Permission.WRITE;
            }
            if (!flag) {
                if ((mod & IFMDataPermissionMod.SameGroup_Own.getMod()) > 0) {
                    flag = true;
                }
            }
        }
        if (flag) {
            return Permission.READ;
        }

        return Permission.NONE;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
