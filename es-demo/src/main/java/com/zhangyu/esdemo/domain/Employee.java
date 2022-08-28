package com.zhangyu.esdemo.domain;

import lombok.Data;

/**
 * @author zhangyu
 * @date 2022/8/22 11:04
 */
@Data
public class Employee {

    private String id;

    private String name;

    private Integer age;

    private Department department;

    private String zhangyu;
}
