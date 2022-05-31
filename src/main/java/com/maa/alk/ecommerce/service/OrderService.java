package com.maa.alk.ecommerce.service;

import com.maa.alk.ecommerce.domain.Order;
import com.maa.alk.ecommerce.domain.OrderItem;
import graphql.schema.DataFetcher;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order getOrderById(Long orderId);

    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    
    List<Order> getAllOrders();

    List<Order> getUserOrders(String email);

    Order postOrder(Order validOrder, Map<Long, Long> perfumesId);

    String deleteOrder(Long orderId);

    DataFetcher<List<Order>> getAllOrdersByQuery();

    DataFetcher<List<Order>> getUserOrdersByEmailQuery();
}
