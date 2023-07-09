package com.zhangyu.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * 自定义环境处理类
 *
 * @author zhangyu
 * @date 2023/7/9 01:55
 */
public class MyEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] profiles = {"config/test.properties", "config/custom.properties", "config/blog.yml"};

        for (String profile : profiles) {
            Resource resource = new ClassPathResource(profile);
            environment.getPropertySources().addLast(loadProfiles(resource));
        }
    }

    private PropertySource<?> loadProfiles(Resource resource) {
        if (!resource.exists()) {
            throw new IllegalArgumentException("资源不存在");
        }
        if (resource.getFilename().endsWith("yml")) {
            return loadYaml(resource);
        } else {
            return loadProperty(resource);
        }
    }

    private PropertySource loadProperty(Resource resource) {
        try {
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            return new PropertiesPropertySource(resource.getFilename(), properties);
        } catch (Exception e) {
            throw new IllegalStateException("加载Property配置文件失败", e);
        }
    }

    private PropertySource loadYaml(Resource resource) {
        try {
            YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
            factoryBean.setResources(resource);

            Properties properties = factoryBean.getObject();
            return new PropertiesPropertySource(resource.getFilename(), properties);
        } catch (Exception e) {
            throw new IllegalStateException("加载Yaml配置文件失败", e);
        }
    }
}
