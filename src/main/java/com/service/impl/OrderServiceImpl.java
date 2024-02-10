package com.service.impl;

import com.entity.Commodity;
import com.entity.Order;
import com.mapper.CommodityMapper;
import com.mapper.OrderMapper;
import com.service.OrderService;
import com.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CommodityMapper commodityMapper;

    @Override
    public Integer createOrder(Order order) {
        // 订单号码
        String orderNo = OrderUtil.getOrderNo();
        order.setOrderId(orderNo);
        return orderMapper.insert(order);
    }

    @Override
    public Integer deleteOrderById(int id) {
        return orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.selectAll();
    }

    @Override
    public List<Order> getOrdersByPage(Integer page, Integer size,String buyerId, String sellerId) {
        Integer begin = (page - 1) * size;
        List<Order> orders = orderMapper.queryPageList(begin, size, buyerId, sellerId);
        List<Commodity> commodities = commodityMapper.selectAll();
        // list转map
        Map<String, String> commodityMap = commodities.stream()
                .collect(Collectors.toMap(commodity -> commodity.getCommid(), commodity -> commodity.getCommdesc()));
        for(Order order:orders){
            String desc = commodityMap.get(order.getCommodityId());
            order.setCommodityDesc(desc);
        }
        return orders;
    }

    @Override
    public Integer getOrderCount(String buyerId, String sellerId) {
        return orderMapper.getCount(buyerId,sellerId).intValue();
    }
}
