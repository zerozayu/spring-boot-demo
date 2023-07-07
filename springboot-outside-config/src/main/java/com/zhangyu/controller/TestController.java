package com.zhangyu.controller;

import com.alibaba.fastjson2.JSON;
import com.zhangyu.domain.Author;
import com.zhangyu.domain.AuthorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试controller
 *
 * @author zhangyu26
 * @date 2023/7/7
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${spring.application.name:}")
    private String applicationName;

    private final Author weijialin;

    public TestController(@Qualifier("weijialin") Author weijialin) {
        this.weijialin = weijialin;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "hello," + applicationName;
    }

    @GetMapping("/author")
    public String weijialin(){
        return JSON.toJSONString(weijialin);
    }
}
