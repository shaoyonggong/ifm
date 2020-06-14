package com.syg.ifmclient.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syg.ifmclient.base.ServiceGateway;
import com.syg.ifmclient.utils.HttpUtil;
import com.syg.ifmclient.utils.ResultUtil;
import com.syg.ifmcommon.result.IFMResult;
import com.syg.ifmcommon.utils.IFMBeanUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public abstract class IFMUrlParamApiClient <T, D> extends AbstractIFMClient<T, D>{
    
    @Autowired
    ServiceGateway serviceGateway;

    @Autowired
    AsyncTemplate<T,D> sync;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public IFMResult<D> process(T param) {
        this.setSg(serviceGateway);

        String url = HttpUtil.findUri(IFMBeanUtil.beanToMap(param), this.getApiUrl());

        this.logRPC(url);

        ResponseEntity<String> responseEntity;
        HttpHeaders requestHeaders = this.addHeaders();

        HttpEntity requestEntity = new HttpEntity(requestHeaders);
        IFMResult isbResult =new IFMResult();
        try {
            responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            isbResult = ResultUtil.result(responseEntity,isbResult,url);
            if(isbResult.getBody()!=null){
                isbResult.setBody(toObject(JSONObject.toJSONString(isbResult.getBody())));
            }
        }catch (Exception e){
            isbResult= BaseResultClient.result(isbResult,e,url);
        }


        return isbResult;
    }

    /**
     * 异步处理
     * @param param 参数对象
     * @return
     */
    public void process(T param, Consumer<IFMResult<D>> sucOrerrFun) {
        this.setSg(serviceGateway);
        String url = HttpUtil.findUri(IFMBeanUtil.beanToMap(param), this.getApiUrl());
        String tracingId= MDC.get("tracingId");
        String number=null;
        if(!StringUtils.isEmpty(tracingId)){
            number=String.valueOf(Integer.valueOf(MDC.get("recordNumber"))+1);
            MDC.put("recordNumber",number);
        }
        this.logRPC(url);
        sync.postForEntity(url,tracingId,number,isbResult -> {
            if(isbResult.getBody()!=null){
                isbResult.setBody(toObject(JSONObject.toJSONString(isbResult.getBody())));
            }
            sucOrerrFun.accept(isbResult);
        },MDC.get("sessionId"));

    }


    protected D toObject (String s){
        Type[] type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        return JSON.parseObject(s,type[1]);
    }
}
