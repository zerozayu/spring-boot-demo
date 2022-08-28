package com.zhangyu.esdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangyu
 * @date 2022/8/25 15:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Poet {
    private Integer age;
    private String name;
    private String poems;
    private String remarks;
}
