package com.syg.ifmacl.interceptor;

import com.alibaba.druid.util.StringUtils;
import com.syg.ifmacl.annotations.IFMDataPermissionACL;
import com.syg.ifmacl.annotations.IFMDpACLGreenChannel;
import com.syg.ifmacl.annotations.IFMPermissionMod;
import com.syg.ifmacl.annotations.IFMPreAuthTableAlias;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Properties;

/**
 * @Description 查询语句的预处理器
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Intercepts(
        {@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})}
)
@Component
public class IFMPreAuthenticationInterceptor implements Interceptor {

    @Value("${acl.owner:owner}")
    private String owner;
    @Value("${acl.owner_group:owner_group}")
    private String owner_group;
    @Value("${acl.owner_role:owner_role}")
    private String owner_role;
    //com.gihub.page查询count需判定的名字的前缀
    private String countSuffix = "_COUNT";
    ;
    @Autowired
    SessionSupport sessionSupport;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        if (!mappedStatement.getSqlCommandType().equals(SqlCommandType.SELECT)) {
            return invocation.proceed();
        }
        //鉴定基本前置处理条件
        Boolean suportFlag = dealPermissionSuport(mappedStatement.getId());
        if (!suportFlag) {
            return invocation.proceed();
        }
        //获取别名值
        String[] methodAndClassName = SessionUtils.spiltMethodAndClassName(mappedStatement.getId());
        Class<?> mapperclass = Class.forName(methodAndClassName[0]);
        Method method = SessionUtils.findMethod(methodAndClassName[1], mapperclass);
        //判定是否为com.gihub的count前置语句
        if (Objects.isNull(method)) {
            method = getPageRealMethod(methodAndClassName[1], mapperclass);
        }
        //获取表别名值
        String tableAliasValue = null;
        IFMPreAuthTableAlias tableAliasAnno = null;
        if (Objects.nonNull(tableAliasAnno = mapperclass.getAnnotation(IFMPreAuthTableAlias.class))) {
            tableAliasValue = tableAliasAnno.value();
        }
        if (Objects.nonNull(method) && Objects.nonNull(tableAliasAnno = method.getAnnotation(IFMPreAuthTableAlias.class))) {
            tableAliasValue = tableAliasAnno.value();
        }

        //获取IFMPermissionMod值
        int mod = 0b000000;
        IFMPermissionMod annoIFMPermissionMod = null;
        if (Objects.nonNull(annoIFMPermissionMod = mapperclass.getAnnotation(IFMPermissionMod.class))) {
            mod = annoIFMPermissionMod.value();
        }
        if (Objects.nonNull(method) && Objects.nonNull(annoIFMPermissionMod = method.getAnnotation(IFMPermissionMod.class))) {
            mod = annoIFMPermissionMod.value();
        }

        //跟据mod 和 别名，确定条件插入语句
        String condition = getDecisionConditions(tableAliasValue, mod);

        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = mappedStatement.getBoundSql(parameter);
            //添加sql条件
            boundSql = addCondition(boundSql, condition);
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
            //添加sql条件
            boundSql = addCondition(boundSql, condition);
            //更新cachekey
            cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
        }
        //执行查询
        return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    /**
     * 根据权限，拼接sql条件
     *
     * @param tableAlias
     * @return
     */
    private String getDecisionConditions(String tableAlias, int mod) throws Exception {
        //确定各个属性权限
        //一，得到拥有者权限认定(因拥有者权限必定可读，所以后续直接加上条件)

        //二，得到角色拥有权限认定
        Permission rolePermission = null;
        if ((mod & IFMDataPermissionMod.Role_Own.getMod()) > 0) {
            rolePermission = Permission.READ;
        }
        //三，得到上级拥有权限认定
        Permission upperGroupPermission = null;
        if ((mod & IFMDataPermissionMod.UpperGroup_Own.getMod()) > 0) {
            upperGroupPermission = Permission.READ;
        }
        //四，得到同僚拥有权限认定
        Permission sameGroupPermission = null;
        if ((mod & IFMDataPermissionMod.SameGroup_Own.getMod()) > 0) {
            sameGroupPermission = Permission.READ;
        }
        //根据session获取要填充的条件表达式
        IFMDataPermissionSupport support = sessionSupport.get();
        //如果没有用户信息就让sql什么也查不出来
        if (Objects.isNull(support)) {
            return " 1=2 ";
        }
        String owners = support.getOwner();
        String ownerGroup = support.getOwnerGroup();
        String ownerRole = support.getOwnerRole();
        if (Objects.isNull(owners) && Objects.isNull(ownerGroup) && Objects.isNull(ownerRole)) {
            return " 1=2 ";
        }
        //获取角色条件
        String rolecon = null;
        if (!StringUtils.isEmpty(ownerRole) && Permission.READ.equals(rolePermission)) {
            rolecon = SessionUtils.getRoleRegexp(ownerRole);
            StringBuilder role =
                    new StringBuilder()
                            .append(tableAlias)
                            .append(".")
                            .append(owner_role)
                            .append(" REGEXP '")
                            .append(rolecon)
                            .append("'");
            rolecon = role.toString();
        }
        //获取上级模糊匹配条件
        String uppercon = null;
        if (!StringUtils.isEmpty(ownerGroup) && Permission.READ.equals(upperGroupPermission)) {
            uppercon = new StringBuilder()
                    .append(tableAlias)
                    .append(".")
                    .append(owner_group)
                    .append(" LIKE '")
                    .append(ownerGroup)
                    .append(",%'")
                    .toString();
        }
        //获取同僚匹配条件
        String samecon = null;
        if (!StringUtils.isEmpty(ownerGroup) && Permission.READ.equals(sameGroupPermission)) {
            samecon = new StringBuilder()
                    .append(tableAlias)
                    .append(".")
                    .append(owner_group)
                    .append("='")
                    .append(ownerGroup)
                    .append("'")
                    .toString();
        }
        //得到最终条件字符串
        StringBuilder sb = new StringBuilder(" (");
        if (!StringUtils.isEmpty(owners)) {
            sb.append(tableAlias)
                    .append(".")
                    .append(owner)
                    .append("='")
                    .append(owners)
                    .append("'");
        } else {
            sb.append(" 1=1 ");
        }
        if (Objects.nonNull(rolecon)) {
            sb.append(" OR ")
                    .append(rolecon);
        }
        if (Objects.nonNull(uppercon)) {
            sb.append(" OR ")
                    .append(uppercon);
        }
        if (Objects.nonNull(samecon)) {
            sb.append(" OR ")
                    .append(samecon);
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * 前置装饰器基本鉴权处理
     *
     * @param id mapper具体到方法的一个字符串路径
     * @return 鉴权的处理结果
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private Boolean dealPermissionSuport(String id)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String[] methodAndClassName = SessionUtils.spiltMethodAndClassName(id);
        Class<?> mapperclass = Class.forName(methodAndClassName[0]);
        IFMDataPermissionACL classAnno = mapperclass.getAnnotation(IFMDataPermissionACL.class);
        //判定mapper接口是否有IFMDataPermissionACL标记
        if (classAnno == null) {
            return false;
        }
        Method method = null;
        //因为mybatis不支持mapper重载方法，所以用方法名字可以唯一确定一组方法
        method = SessionUtils.findMethod(methodAndClassName[1], mapperclass);
        //判定是否为com.gihub的count前置语句
        if (Objects.isNull(method)) {
            method = getPageRealMethod(methodAndClassName[1], mapperclass);
        }
        //判定对应方法上是否存在IFMDpACLGreenChannel
        if (Objects.nonNull(method) && Objects.nonNull(method.getAnnotation(IFMDpACLGreenChannel.class))) {
            return false;
        }
        //判定mapper的类上和方法上有没有给出过滤条件的表名的必须接口
        if (Objects.isNull(mapperclass.getAnnotation(IFMPreAuthTableAlias.class)) && Objects.nonNull(method) && Objects.isNull(method.getAnnotation(IFMPreAuthTableAlias.class))) {
            return false;
        }
        //判定类或方法上是否存在IFMPermissionMod注解
        if (Objects.isNull(mapperclass.getAnnotation(IFMPermissionMod.class)) && Objects.nonNull(method) && Objects.isNull(method.getAnnotation(IFMPermissionMod.class))) {
            return false;
        }
        return true;
    }

    private BoundSql addCondition(BoundSql boundSql, String condition) throws NoSuchFieldException, IllegalAccessException {
        String newsql = SessionUtils.addFilterCondition(boundSql.getSql(), condition);
        Field sql = BoundSql.class.getDeclaredField("sql");
        sql.setAccessible(true);
        sql.set(boundSql, newsql);
        return boundSql;
    }

    private Method getPageRealMethod(String methodName, Class clazz) {
        String name = methodName;
        if (name.length() > countSuffix.length()) {
            if (countSuffix.equals(name.substring(name.length() - countSuffix.length(), name.length()))) {
                return SessionUtils.findMethod(name.substring(0, name.length() - countSuffix.length()), clazz);
            }
        }
        return null;
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
