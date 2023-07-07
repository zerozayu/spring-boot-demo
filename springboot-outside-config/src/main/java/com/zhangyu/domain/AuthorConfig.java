package com.zhangyu.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置类
 *
 * @author zhangyu26
 * @date 2023/7/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties("author")
@Profile("prod")
@Configuration
public class AuthorConfig {
    private String name;
    private String age;
    private String desc;

    @Bean("zhangyu")
    public Author zhangyu(){
        Author zhangyu = new Author();
        zhangyu.setName(this.name);
        zhangyu.setAge(this.age);
        zhangyu.setDesc(this.desc);

        return zhangyu;
    }

    @Bean("weijialin")
    public Author weijialin(){
        Author weijialin = new Author();
        weijialin.setName(this.name + "weijialin");
        weijialin.setAge(this.age);
        weijialin.setDesc(this.desc);

        return weijialin;
    }
}
