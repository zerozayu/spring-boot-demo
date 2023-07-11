package com.zhangyu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * nacos测试controller类
 *
 * @author zhangyu26
 * @date 2023/7/11
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class NacosConfigController {

    @Value("${author.name}")
    private String name;

    @GetMapping("/author")
    public String author() {
        return name;
    }

    @GetMapping("/example")
    public Boolean getExample() {
//        return useLocalCache;
        return true;
    }


}
