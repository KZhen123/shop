package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)//链式写法
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 分类id
     * */
    private Integer id;

    /**
     * 分类名称
     * */
    private String name;

    /**
     * 分类描述
     * */
    private String des;
}
