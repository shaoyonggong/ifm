package com.syg.ifmacl.interceptor;

import com.syg.ifmacl.annotations.IFMDataPermissionACL;
import com.syg.ifmacl.annotations.IFMDpACLGreenChannel;
import com.syg.ifmacl.support.IFMDataPermissionSupport;
import com.syg.ifmacl.support.SessionSupport;
import com.syg.ifmacl.utils.SessionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Description 插入数据时的预处理器
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
@Component
public class IFMDataAuthorizeInterceptor implements Interceptor {

    @Autowired
    SessionSupport sessionSupport;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];

        if (!SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }
        String id = mappedStatement.getId();
        //一,不代理多数据模型多参数,二,如果参数是一个collection批处理就遍历集合鉴权赋值
        if (args[1] instanceof Map) {
            //处理批处理
            Map map = (Map) args[1];
            if (map.containsKey("collection")) {
                Object obj = map.get("collection");
                if (obj instanceof Collection) {

                    Collection collection = (Collection) obj;
                    //进行鉴权处理
                    boolean flag = false;
                    if (collection != null && collection.size() > 0) {
                        flag = dealPermissionSuport(id, collection.iterator().next());
                    }
                    if (!flag) {
                        return invocation.proceed();
                    }
                    Iterator it = collection.iterator();
                    IFMDataPermissionSupport support = sessionSupport.get();
                    //遍历结果集塞参数
                    while (it.hasNext()) {
                        IFMDataPermissionSupport po = (IFMDataPermissionSupport) it.next();
                        if (support != null) {
                            po.setOwner(support.getOwner());
                            if (support.getOwnerRole() != null) {
                                po.setOwnerRole("," + support.getOwnerRole() + ",");
                            }
                            po.setOwnerGroup(support.getOwnerGroup());
                        }
                    }

                }
            }
            return invocation.proceed();
        } else {
            //对ACl权限进行处理

            boolean flag = dealPermissionSuport(id, args[1]);
            if (!flag) {
                return invocation.proceed();
            }

            IFMDataPermissionSupport po = (IFMDataPermissionSupport) args[1];
            IFMDataPermissionSupport support = sessionSupport.get();
            if (support != null) {
                po.setOwner(support.getOwner());
                if (support.getOwnerRole() != null) {
                    po.setOwnerRole("," + support.getOwnerRole() + ",");
                }
                po.setOwnerGroup(support.getOwnerGroup());
            }
            return invocation.proceed();
        }
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

    /**
     * 插入定权和查询定权通用处理器
     *
     * @param id    mapper具体到方法的一个字符串路径
     * @param param 方法参数数据模型
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    static boolean dealPermissionSuport(String id, Object param)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String[] methodAndClassName = SessionUtils.spiltMethodAndClassName(id);
        Class<?> mapperclass = Class.forName(methodAndClassName[0]);
        //判定mapper接口是否有IFMDataPermissionACL标记
        if (Objects.isNull(mapperclass.getAnnotation(IFMDataPermissionACL.class))) {
            return false;
        }
        //判定对应方法上是否存在IFMDpACLGreenChannel
        Method method = null;
        if (Objects.nonNull(method = SessionUtils.findMethod(methodAndClassName[1], mapperclass)) && Objects.nonNull(method.getAnnotation(IFMDpACLGreenChannel.class))) {
            return false;
        }
        //判定数据模型是否实现了IFMDataPermissionSupport
        if (!(param instanceof IFMDataPermissionSupport)) {
            return false;
        }
        return true;
    }

}
