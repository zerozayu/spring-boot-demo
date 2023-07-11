package com.zhangyu.controller;

import com.alibaba.fastjson2.JSON;
import com.zhangyu.config.AuthorConfig;
import com.zhangyu.domain.Author;
import com.zhangyu.log.annotion.MyLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 测试controller
 *
 * @author zhangyu26
 * @date 2023/7/7
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final Author weijialin;

    private final AuthorConfig authorConfig;

    public TestController(@Qualifier("weijialin") Author weijialin, AuthorConfig authorConfig) {
        this.weijialin = weijialin;
        this.authorConfig = authorConfig;
    }

    @Value("${spring.application.name:}")
    private String applicationName;

    @Value("${loading-sequence:无数据}")
    private String sequence;

    @Value("${bbbb.a1}")
    private String custom;

    /**
     * 测试自定义starter
     *
     * @return
     */
    @MyLog
    @GetMapping("/annotation/mylog")
    public String testAnnotation() {
        return "这是一条发生在" + LocalDateTime.now() + "的日志";
    }

    /**
     * 测试自定义配置文件
     *
     * @return
     */
    @GetMapping("/custom")
    public String testCustomProperties() {
        return custom;
    }

    /**
     * 测试配置文件加载顺序
     *
     * @return
     */
    @GetMapping("/sequence")
    public String sequence() {
        return JSON.toJSONString(sequence);
    }

    @GetMapping("/author_config")
    public String authorConfig() {
        return JSON.toJSONString(authorConfig);
    }

    @GetMapping("/author")
    public String weijialin() {
        return JSON.toJSONString(weijialin);
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello," + applicationName;
    }


}
