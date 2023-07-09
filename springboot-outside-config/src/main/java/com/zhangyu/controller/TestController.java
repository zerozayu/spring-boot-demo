package com.zhangyu.controller;

import com.alibaba.fastjson2.JSON;
import com.zhangyu.config.AuthorConfig;
import com.zhangyu.domain.Author;
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

    private final AuthorConfig authorConfig;

    public TestController(@Qualifier("weijialin") Author weijialin, AuthorConfig authorConfig) {
        this.weijialin = weijialin;
        this.authorConfig = authorConfig;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "hello," + applicationName;
    }

    @GetMapping("/author")
    public String weijialin(){
        return JSON.toJSONString(weijialin);
    }

    @GetMapping("/author_config")
    public String authorConfig(){
        return JSON.toJSONString(authorConfig);
    }
}
