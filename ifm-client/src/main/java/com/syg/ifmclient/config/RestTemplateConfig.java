package com.syg.ifmclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Configuration
public class RestTemplateConfig {
    @Value("${timeoutTime:240000}")
    private int timeoutTime;

    @Bean
    public SimpleClientHttpRequestFactory httpClientFactory() {
        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout(timeoutTime);
        httpRequestFactory.setConnectTimeout(timeoutTime);
        return httpRequestFactory;
    }


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(SimpleClientHttpRequestFactory httpClientFactory) {
        RestTemplate restTemplate=new RestTemplate(httpClientFactory);
        restTemplate.setErrorHandler(new ThrowErrorHandler());
        return restTemplate;
    }
}
