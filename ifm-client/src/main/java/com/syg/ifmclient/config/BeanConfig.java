package com.syg.ifmclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Configuration
public class BeanConfig {
    @Bean
    public CustomBeanDefinitionRegistry customBeanDefinitionRegistry() {

        return new CustomBeanDefinitionRegistry();
    }

}
