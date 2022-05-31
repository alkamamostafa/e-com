package com.maa.alk.ecommerce.mapper;

import com.maa.alk.ecommerce.domain.Order;
import com.maa.alk.ecommerce.dto.order.OrderItemResponse;
import com.maa.alk.ecommerce.dto.order.OrderRequest;
import com.maa.alk.ecommerce.dto.order.OrderResponse;
import com.maa.alk.ecommerce.exception.InputFieldException;
import com.maa.alk.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CommonMapper commonMapper;
    private final OrderService orderService;
    
    public OrderResponse getOrderById(Long orderId) {
        return commonMapper.convertToResponse(orderService.getOrderById(orderId), OrderResponse.class);
    }
    
    public List<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        return commonMapper.convertToResponseList(orderService.getOrderItemsByOrderId(orderId), OrderItemResponse.class);
    }

    public List<OrderResponse> getAllOrders() {
        return commonMapper.convertToResponseList(orderService.getAllOrders(), OrderResponse.class);
    }

    public List<OrderResponse> getUserOrders(String email) {
        return commonMapper.convertToResponseList(orderService.getUserOrders(email), OrderResponse.class);
    }

    public String deleteOrder(Long orderId) {
        return orderService.deleteOrder(orderId);
    }

    public OrderResponse postOrder(OrderRequest orderRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        Order order = orderService.postOrder(commonMapper.convertToEntity(orderRequest, Order.class), orderRequest.getPerfumesId());
        return commonMapper.convertToResponse(order, OrderResponse.class);
    }
}
