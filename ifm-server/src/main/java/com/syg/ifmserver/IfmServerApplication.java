package com.syg.ifmserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shaoyonggong
 * @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
 */
@SpringBootApplication
public class IfmServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IfmServerApplication.class, args);
        System.out.println("---------start-------------");
    }

}
