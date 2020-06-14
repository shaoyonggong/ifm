package com.syg.ifmclient.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Component
public class ServiceGateway {

    @Value("${gateway.protocol:http}")
    protected String protocol;

    public String getProtocol(){
        return this.protocol;
    }


}
