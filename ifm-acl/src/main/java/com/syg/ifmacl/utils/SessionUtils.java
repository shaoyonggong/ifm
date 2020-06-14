package com.syg.ifmacl.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
public class SessionUtils {
    public static String[] spiltMethodAndClassName(String arg) {
        int index = arg.lastIndexOf(".");
        String className = arg.substring(0, index);
        String methodName = arg.substring(index + 1, arg.length());
        String[] s = new String[2];
        s[0] = className;
        s[1] = methodName;
        return s;
    }

    //判定数据owner_role字段里面是否包含此角色
    public static Boolean isContains(String s, String ownerrole) {
        if (ownerrole.length() <= 1) {
            return false;
        }
        String[] split = ownerrole.split(",");
        for (int i = 0; i < split.length; i++) {
            if (s.contains("," + split[i] + ",")) {
                return true;
            }
        }
        return false;
    }

    public static Method findMethod(String methodname, Class clazz) {
        Method method = null;
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(methodname)) {
                method = m;
                break;
            }
        }
        return method;
    }

    //获取角色对应的正则表达式
    public static String getRoleRegexp(String roleStr) {
        if (roleStr == null) {
            return null;
        }
        String[] split = roleStr.split("[,]");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            s.append("|," + split[i] + ",");
        }
        s.delete(0, 1);
        return s.toString();
    }

    //对于条件的正确性此方法并不能判断（如果sql里面有别名.的判断必须自己去取得数据库的sql别名拼接sql）
    public static String addFilterCondition(String sql, String condition) {
        char[] c = sql.toCharArray();
        //统计"（）"的数量如果有（（））默认为一个（）
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '(') {
                list.add(i);
            }
            int count = 1;
            for (int j = i; j < c.length; j++) {
                if (c[j] == '(') {
                    count++;
                }
                if (c[j] == ')') {
                    count--;
                }
                if (count == 0) {
                    list.add(j);
                    i = j;
                    break;
                }
            }
        }
        String[] placeholderStr = new String[list.size() / 2];
        int index = 0;
        StringBuilder sb = new StringBuilder();
        //拼接字符串和存储（）里面占位字符串
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                sb.append(sql.substring(0, list.get(i)));
                continue;
            }
            if (i % 2 == 1) {
                placeholderStr[index] = sql.substring(list.get(i - 1) + 1, list.get(i));
                index++;
                sb.append("($AISYG$)");
            }
            if (i == list.size() - 1) {
                sb.append(sql.substring(list.get(i) + 1, sql.length()));
                continue;
            }
            if (i % 2 != 1) {
                sb.append(sql.substring(list.get(i - 1) + 1, list.get(i)));
            }
        }
        if (placeholderStr.length == 0) {
            sql = sb.append(sql).toString().toLowerCase();
        } else {
            sql = sb.toString().toLowerCase();
        }
        //判断这条简单sql是否有where关键字对if else作出相应的填充条件动作
        StringBuilder temStr = new StringBuilder();
        if (sql.contains("where")) {
            index = sql.lastIndexOf("where");
            temStr.append(" ").append(condition).append(" AND");
            sb.insert(index + 5, temStr);
            //
        } else {
            String[] s;
            if ((s = sql.split("\\s+(group)\\s+(by)\\s+")).length > 1) {
                temStr.append(" WHERE ").append(condition);
                sb.insert(s[0].length(), temStr);
            } else if ((s = sql.split("\\s+(order)\\s+(by)\\s+")).length > 1) {
                temStr.append(" WHERE ").append(condition);
                sb.insert(s[0].length(), temStr);
            } else if ((s = sql.split("\\s+(limit)\\s+")).length > 1) {
                temStr.append(" WHERE ").append(condition);
                sb.insert(s[0].length(), temStr);
            } else {
                sb.append(" where ")
                        .append(condition);
            }
        }
        //填充（）占位字符串到添加了条件到原sql中
        for (int i = 0; i < placeholderStr.length; i++) {
            int indexOf = sb.indexOf("($AISYG$)");
            sb.replace(indexOf + 1, indexOf + 11, "");
            sb.insert(indexOf + 1, placeholderStr[i]);

        }
        return sb.toString();
    }
}
