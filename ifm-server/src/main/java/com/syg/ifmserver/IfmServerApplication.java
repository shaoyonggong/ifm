package com.syg.ifmserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author shaoyonggong
 * @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
 */
@MapperScan("com.syg.ifmserver.dao")
@ComponentScan("com.syg.**")
@SpringBootApplication
@EnableSwagger2
public class IfmServerApplication {

    @PostConstruct
    void started(){
        System.setProperty("user.timezone","Asia/Beijing");
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Beijing"));
    }

    public static void main(String[] args) {
        SpringApplication.run(IfmServerApplication.class, args);
        System.out.println("---------start-------------");
    }

}
