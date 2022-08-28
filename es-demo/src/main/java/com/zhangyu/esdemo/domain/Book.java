package com.zhangyu.esdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangyu
 * @date 2022/8/22 17:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Book {
    private String bName;
    private String bAuthor;
    private String bPress;
    private String bRemark;
}
