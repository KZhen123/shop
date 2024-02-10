package com.service;

import com.entity.Order;

import java.util.List;

public interface OrderService {

    Integer createOrder(Order order);

    Integer deleteOrderById(int id);

    List<Order> getAllOrders();

    List<Order> getOrdersByPage(Integer page, Integer size, String buyerId, String sellerId);

    Integer getOrderCount(String buyerId, String sellerId);
}

