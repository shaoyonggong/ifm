package com.syg.ifmcommon.utils;

import com.google.common.collect.Maps;
import com.syg.ifmcommon.exception.IFMException;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class IFMBeanUtil {
    /**
     * 将对象转化为Map
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if (null == beanMap.get(key)) {
                    continue;
                }
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 方便的工具,可以把一个polist转化成dtolist,前提是他们的属性接近(只能copy属性交集).
     * 将po对象的list转化成属性接近的dto对象列表.
     */
    public static <T,D> List<T> convertToDtoList(Class<T> dtoClass, List<D> poList) throws IFMException {

        List<T> dtoList = new ArrayList<>(poList.size());

        try {
            for (D tPo : poList) {
                T dto=dtoClass.newInstance();
                BeanUtils.copyProperties(tPo,dto);
                dtoList.add(dto);
            }
        } catch (Exception e) {
            throw new IFMException("0", "系统异常:在尝试convertToDtoList时发生了错误, 请检查PO和DTO是否可以相互copy属性",e);
        }

        return dtoList;
    }
}
