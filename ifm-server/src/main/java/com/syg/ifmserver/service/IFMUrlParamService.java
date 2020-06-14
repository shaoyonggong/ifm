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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public abstract class IFMUrlParamService<T, D> extends BaseIFMService<T, D>{
    @PostMapping("/")
    public final IFMResult<D> post(@Validated @ModelAttribute T param, BindingResult bindingResult, HttpServletRequest request, @RequestHeader HttpHeaders headers) throws Exception {
        IFMResult<D> ifmResult = new IFMResult<>();
        // 调用通用处理方法,并埋入一个模板处理过程, 在dealService的处理最后,会实际调用开发者实现的process方法完成对param的处理.
        ServiceLog serviceLog=new ServiceLog();
        serviceLog.setStartTime(System.currentTimeMillis());
        serviceLog.setClientIp(IpUtil.getIpAddr(request));
        serviceLog.setId(MDC.get("traceRootId"));
        serviceLog.setRequestType("URL");
        this.dealService(request.getServletPath(), ifmResult, toObject(JSONObject.toJSONString(param)),serviceLog,headers, bindingResult, this::process);
        return ifmResult;
    }

    protected T toObject (String s){
        Type[] type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        return JSON.parseObject(s,type[0]);
    }
}
