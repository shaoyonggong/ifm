package com.syg.ifmclient.client;

import com.syg.ifmclient.utils.ResultUtil;
import com.syg.ifmcommon.result.IFMResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.function.Consumer;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncTemplate<T, D> {

    @Resource
    RestTemplate restTemplate;

    /**
     * 请求方式json格式
     * @param url
     * @param param
     * @param sucOrerrFun
     */
    @Async
    public  void postForEntity(String url, String id, String number, T param, Consumer<IFMResult<D>> sucOrerrFun, String sessionId){
        ResponseEntity<String> responseEntity;
        //headers
        HttpHeaders requestHeaders = new HttpHeaders();
        if(!StringUtils.isEmpty(id)){
            requestHeaders.add("tracingId", id);
            requestHeaders.add("recordNumber", number);
            requestHeaders.add("issync", "1");
        }
        if(!StringUtils.isEmpty(sessionId)){
            requestHeaders.add("cookie", "sessionId="+sessionId);
        }

        HttpEntity requestEntity = new HttpEntity(param, requestHeaders);
        IFMResult<D> result = new IFMResult();
        try {
            responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            result= ResultUtil.result(responseEntity,result,url);
            if(!StringUtils.isEmpty(id)){
                MDC.put("tracingId",id);
                MDC.put("recordNumber",String.valueOf(Long.valueOf(number)));
            }
            if(!StringUtils.isEmpty(sessionId)){
                MDC.put("sessionId",sessionId);
            }
            sucOrerrFun.accept(result);
        }catch (Exception e){
            log.error("执行异步json请求时出现异常:"+url);
            e.printStackTrace();

        }
    }

    @Async
    public  void postForEntity(String url,String id,String number, Consumer<IFMResult<D>> sucOrerrFun,String sessionId){
        ResponseEntity<String> responseEntity;
        HttpHeaders requestHeaders = new HttpHeaders();
        if(!StringUtils.isEmpty(id)){
            requestHeaders.add("tracingId", id);
            requestHeaders.add("recordNumber", number);
            requestHeaders.add("issync", "1");
        }
        if(!StringUtils.isEmpty(sessionId)){
            requestHeaders.add("cookie", "sessionId="+sessionId);
        }
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        IFMResult<D> result = new IFMResult();
        try {
            responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            result = ResultUtil.result(responseEntity, result, url);
            if(!StringUtils.isEmpty(id)){
                MDC.put("tracingId",id);
                MDC.put("recordNumber",String.valueOf(Long.valueOf(number)));
            }
            if(!StringUtils.isEmpty(sessionId)){
                MDC.put("sessionId",sessionId);
            }
            sucOrerrFun.accept(result);
        }catch (Exception e){
            log.error("执行异步url请求是出现异常:"+url);
            e.printStackTrace();
        }
    }
}
