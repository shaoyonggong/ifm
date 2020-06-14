package com.syg.idempotent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syg.idempotent.cache.CacheManager;
import com.syg.idempotent.cache.RedisManager;
import com.syg.ifmcommon.annotations.Idempotent;
import com.syg.ifmcommon.dto.IFMResultErrorInfo;
import com.syg.ifmcommon.result.IFMResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.klock.model.LockInfo;
import org.springframework.boot.autoconfigure.klock.model.LockType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/15
 */
@Aspect
@Component
@Service
public class IdempotentAop {

    private static Logger logger = LoggerFactory.getLogger(IdempotentAop.class);

    @Autowired
    CacheManager cacheManager;

    @Autowired
    HttpServletRequest request;

    @Value("${spring.redis.idemExpire:1440}")
    private long expire;

    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        return className.equals(String.class) || className.equals(Integer.class) || className.equals(Byte.class) || className.equals(Long.class) || className.equals(Double.class) || className.equals(Float.class) || className.equals(Character.class) || className.equals(Short.class) || className.equals(Boolean.class);
    }

    @Pointcut("execution(  * com.syg.*..spi..*Spi.process(..))")
    public void webPointCut() {
    }

    @Around("webPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容

        String messageId = request.getHeader("messageId");
        Object target = joinPoint.getTarget();
        EnableIdempotent annotation = target.getClass().getAnnotation(EnableIdempotent.class);

        if (annotation != null || !StringUtils.isEmpty(messageId)) {

            RequestMapping requestMapping = target.getClass().getAnnotation(RequestMapping.class);
            String path = requestMapping.value()[0];
            String key = "ifm-idempotent_" + path + "_";
            String string = key;
            List<Object> params = Arrays.asList(joinPoint.getArgs());
            String md5String = StringUtils.isEmpty(messageId) ? JSON.toJSONString(params) : messageId;
            String md5 = DigestUtils.md5DigestAsHex(md5String.getBytes());

            logger.info("开始幂等校验！");
            String redisKey;
            if (annotation != null) {
                if (cacheManager instanceof RedisManager) {

                    LockInfo lockInfo = new LockInfo(LockType.Reentrant, md5, 9223372036854775807L, annotation.synctime() == 0 ? 5L : annotation.synctime());

                    if (((RedisManager) cacheManager).isRedis(lockInfo, md5, params, annotation.synctime() == 0 ? 60 : annotation.synctime())) {
                        IFMResult ifmResult = new IFMResult();
                        ifmResult.markRefused();
                        ifmResult.setException(new IFMResultErrorInfo("0", "正在执行，请勿重复请求"));
                        return ifmResult;
                    }
                }
                if (annotation.validateType() == ValidateType.MESSAGEID && !StringUtils.isEmpty(messageId)) {
                    string += messageId;
                } else {
                    if (params.get(0) instanceof List && annotation.validateList()) {

                        List<Object> objects = new ArrayList<>();
                        for (int i = 0; i < ((List) params.get(0)).size(); i++) {
                            StringBuilder temp = new StringBuilder();
                            List<Field> fields = getAllField(((List) params.get(0)).get(i));
                            for (Field field : fields) {
                                Idempotent notEmpty = field.getAnnotation(Idempotent.class);
                                if (notEmpty != null) {
                                    for (int j = 0; j < notEmpty.value().length; j++) {
                                        if (notEmpty.value()[j].equals(requestMapping.value()[0])) {
                                            field.setAccessible(true);
                                            String value = field.get(((List) params.get(0)).get(i)) == null ? null : IdempotentAop.isBaseType(field.get(((List) params.get(0)).get(i))) ? field.get(((List) params.get(0)).get(i)).toString() : JSON.toJSONString(field.get(((List) params.get(0)).get(i)));
                                            temp.append(value);
                                        }
                                    }
                                }
                            }
                            if (cacheManager.get(temp.toString()) == null) {
                                objects.add(((List) params.get(0)).get(i));
                                cacheManager.set(temp.toString(), "temp", annotation.expire() == 0 ? expire : annotation.expire(), annotation.timeUnit());
                            }
                        }
                        try {
                            Object proceed = joinPoint.proceed(new List[]{objects});
                            cacheManager.delete(md5);
                            return proceed;
                        } catch (Exception e) {
                            cacheManager.delete(md5);
                            throw e;
                        }

                    }
                    string = getValidateString(string, key, params.get(0), path, annotation);
                }

                if (key.equals(string)) {
                    cacheManager.delete(md5);
                    return joinPoint.proceed();
                }
                redisKey = string;
            } else {
                redisKey = messageId;
            }
            Object result = cacheManager.get(redisKey);

            if (result != null) {
                IFMResult ifmResult;
                if (result instanceof String) {
                    ifmResult = JSONObject.parseObject((String) result, IFMResult.class);
                } else {
                    ifmResult = (IFMResult) result;
                }
                if (!IFMResult.SUCCESS_CODE.equals(ifmResult.getCode())) {
                    try {
                        Object proceed = joinPoint.proceed();
                        cacheManager.delete(redisKey);
                        cacheManager.set(redisKey, proceed, (annotation == null || annotation.expire() == 0) ? expire : annotation.expire(), annotation == null ? TimeUnit.MINUTES : annotation.timeUnit());
                        cacheManager.delete(md5);

                        return proceed;
                    } catch (Exception e) {
                        cacheManager.delete(md5);

                        throw e;
                    }
                } else {
                    cacheManager.delete(md5);
                    return result;
                }
            }
            try {
                Object proceed = joinPoint.proceed();
                cacheManager.set(redisKey, proceed, (annotation == null || annotation.expire() == 0) ? expire : annotation.expire(), annotation == null ? TimeUnit.MINUTES : annotation.timeUnit());
                cacheManager.delete(md5);
                return proceed;
            } catch (Exception e) {
                cacheManager.delete(md5);
                throw e;
            }
        }
        return joinPoint.proceed();

    }

    public String getValidateString(String string, String key, Object params, String path, EnableIdempotent enableIdempotent) throws IllegalAccessException {
        if (params instanceof List) {
            StringBuilder stringBuilder = new StringBuilder(string);
            for (int i = 0; i < ((List) params).size(); i++) {
                List<Field> fields = getAllField(((List) params).get(i));

                for (Field field : fields) {
                    Idempotent notEmpty = field.getAnnotation(Idempotent.class);
                    if (notEmpty != null) {
                        for (int j = 0; j < notEmpty.value().length; j++) {
                            if (notEmpty.value()[j].equals(path)) {
                                field.setAccessible(true);
                                String value = field.get(((List) params).get(i)) == null ? null : IdempotentAop.isBaseType(field.get(((List) params).get(i))) ? field.get(((List) params).get(i)).toString() : JSON.toJSONString(field.get(((List) params).get(i)));
                                stringBuilder.append(value);
                            }
                        }
                    }
                }
            }
            string = stringBuilder.toString();
        } else {
            List<Field> fields = getAllField(params);
            StringBuilder stringBuilder = new StringBuilder(string);
            for (Field field : fields) {
                Idempotent notEmpty = field.getAnnotation(Idempotent.class);
                if (notEmpty != null) {
                    for (int i = 0; i < notEmpty.value().length; i++) {
                        if (notEmpty.value()[i].equals(path)) {
                            field.setAccessible(true);
                            String value = field.get(params) == null ? null : IdempotentAop.isBaseType(field.get(params)) ? field.get(params).toString() : JSON.toJSONString(field.get(params));
                            stringBuilder.append(value);
                        }
                    }
                }
            }
            string = stringBuilder.toString();
        }
        if (key.equals(string)) {
            String[] annotationValue = enableIdempotent.value();
            if (params instanceof List) {
                StringBuilder stringBuilder = new StringBuilder(string);
                for (int k = 0; k < ((List) params).size(); k++) {
                    for (String s : annotationValue) {
                        Field field;
                        try {
                            field = getFieldByName(((List) params).get(k), s);
                        } catch (Exception e) {
                            continue;
                        }
                        field.setAccessible(true);
                        String value = field.get(((List) params).get(k)) == null ? null : IdempotentAop.isBaseType(field.get(((List) params).get(k))) ? field.get(((List) params).get(k)).toString() : JSON.toJSONString(field.get(((List) params).get(k)));
                        stringBuilder.append(value);
                    }
                }
                string = stringBuilder.toString();
            } else {
                StringBuilder stringBuilder = new StringBuilder(string);
                for (String s : annotationValue) {
                    Field field;
                    try {
                        field = getFieldByName(params, s);
                    } catch (Exception e) {
                        continue;
                    }
                    field.setAccessible(true);
                    String value = field.get(params) == null ? null : IdempotentAop.isBaseType(field.get(params)) ? field.get(params).toString() : JSON.toJSONString(field.get(params));
                    stringBuilder.append(value);
                }
                string = stringBuilder.toString();
            }
        }
        return string;
    }

    /**
     * 获取类与父类所有属性
     */
    private List<Field> getAllField(Object model) {
        Class clazz = model.getClass();
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private Field getFieldByName(Object model, String name) {

        List<Field> fields = getAllField(model);

        return fields.stream().filter(e -> e.getName().equals(name)).collect(Collectors.toList()).get(0);
    }
}
