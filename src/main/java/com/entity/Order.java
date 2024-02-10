package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)//链式写法
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String buyerId;

    private String sellerId;

    private String commodityId;

    private String commodityName;

    private Double price;

    private Date time;

    private String receiveName;

    private String receivePhone;

    private String address;

    /**
     * 使用时间戳生成的订单号
     * */
    private String orderId;

    /**
     * 商品描述 通过联合查询得到
     * */
    private String commodityDesc;
}
