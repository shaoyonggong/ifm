package com.syg.ifmclient.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syg.ifmclient.client.BaseResultClient;
import com.syg.ifmcommon.result.IFMResult;
import org.springframework.http.ResponseEntity;

/**
 * @Description 包装返回值
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class ResultUtil {
    /**
     * 包装返回值
     * @param responseEntity
     * @param result
     * @return
     */
    public static IFMResult result(ResponseEntity<String> responseEntity, IFMResult result, String url){
        if(200 == responseEntity.getStatusCode().value()){
            result= JSON.parseObject(responseEntity.getBody(),IFMResult.class);
        }else{
            JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
            result= BaseResultClient.result(result,"状态码:"+jsonObject.getString("status")+",异常:"+jsonObject.getString("error")+",内容:"+jsonObject.getString("message"),url);
        }
        return result;
    }
}
