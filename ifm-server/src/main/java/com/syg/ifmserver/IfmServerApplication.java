package com.syg.ifmserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author shaoyonggong
 * @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
 */
@MapperScan("com.syg.ifmserver.dao")
@ComponentScan("com.syg.**")
@SpringBootApplication
@EnableSwagger2
public class IfmServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfmServerApplication.class, args);
        System.out.println("---------start-------------");
    }

}
