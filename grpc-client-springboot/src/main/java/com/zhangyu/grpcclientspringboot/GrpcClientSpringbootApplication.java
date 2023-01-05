package com.zhangyu.grpcclientspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.zhangyu.news")
public class GrpcClientSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientSpringbootApplication.class, args);
    }

}
