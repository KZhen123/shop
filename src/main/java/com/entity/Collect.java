package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车
 * */
@AllArgsConstructor//全参构造
@NoArgsConstructor//无参构造
@Data
@Accessors(chain = true)//链式写法
public class Collect implements Serializable {

    private static final long serialVersionUID = 1L;


    private String id;
    /**
     * 商品id
     */
    private String commid;
    /**
     * 商品名
     */
    private String commname;
    /**
     * 商品描述
     */
    private String commdesc;

    private Date soldtime;
    /**
     * 商品用户id
     */
    private String cmuserid;
    /**
     * 商品所在学校
     */
    private String school;
    /**
     * 用户id
     */
    private String couserid;
}
