package com.syg.ifmserver.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syg.ifmcommon.dto.ServiceLog;
import com.syg.ifmcommon.result.IFMResult;
import com.syg.ifmserver.utils.IpUtil;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public abstract class IFMJsonParamService <T, D> extends BaseIFMService<T, D>{
    
    @PostMapping("/")
    public final IFMResult<D> post(@Validated @RequestBody T param, BindingResult bindingResult, HttpServletRequest request, @RequestHeader HttpHeaders headers) throws Exception {
        IFMResult<D> ifmResult = new IFMResult<>();
        ServiceLog serviceLog=new ServiceLog();
        serviceLog.setStartTime(System.currentTimeMillis());
        serviceLog.setClientIp(IpUtil.getIpAddr(request));
        serviceLog.setId(MDC.get("traceRootId"));
        serviceLog.setRequestType("JSON");
        // lambada表达式, 命令回调函数执行delete抽象方法.
        this.dealService(request.getServletPath(), ifmResult, toObject(JSONObject.toJSONString(param)),serviceLog,headers, bindingResult, this::process);
        return ifmResult;
    }
    protected T toObject (String s){
        Type[] type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        return JSON.parseObject(s,type[0]);
    }
}
