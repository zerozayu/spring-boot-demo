package com.zhangyu.grpcserverspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zhangyu.news")
// @ComponentScan("com.zhangyu.news")
public class GrpcServerSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerSpringbootApplication.class, args);
    }

}
