package com.maa.alk.ecommerce.service.Impl;

import com.maa.alk.ecommerce.domain.Order;
import com.maa.alk.ecommerce.domain.OrderItem;
import com.maa.alk.ecommerce.domain.Perfume;
import com.maa.alk.ecommerce.exception.ApiRequestException;
import com.maa.alk.ecommerce.repository.OrderItemRepository;
import com.maa.alk.ecommerce.repository.OrderRepository;
import com.maa.alk.ecommerce.repository.PerfumeRepository;
import com.maa.alk.ecommerce.service.OrderService;
import com.maa.alk.ecommerce.service.email.MailSender;

import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PerfumeRepository perfumeRepository;
    private final MailSender mailSender;

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiRequestException("Order not found.", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<Order> getUserOrders(String email) {
        return orderRepository.findOrderByEmail(email);
    }

    @Override
    @Transactional
    public Order postOrder(Order validOrder, Map<Long, Long> perfumesId) {
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : perfumesId.entrySet()) {
            Perfume perfume = perfumeRepository.findById(entry.getKey()).get();
            OrderItem orderItem = new OrderItem();
            orderItem.setPerfume(perfume);
            orderItem.setAmount((perfume.getPrice() * entry.getValue()));
            orderItem.setQuantity(entry.getValue());
            orderItemList.add(orderItem);
            orderItemRepository.save(orderItem);
        }
        order.getOrderItems().addAll(orderItemList);
        order.setTotalPrice(validOrder.getTotalPrice());
        order.setFirstName(validOrder.getFirstName());
        order.setLastName(validOrder.getLastName());
        order.setCity(validOrder.getCity());
        order.setAddress(validOrder.getAddress());
        order.setPostIndex(validOrder.getPostIndex());
        order.setEmail(validOrder.getEmail());
        order.setPhoneNumber(validOrder.getPhoneNumber());
        orderRepository.save(order);

        String subject = "Order #" + order.getId();
        String template = "order-template";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("order", order);
        mailSender.sendMessageHtml(order.getEmail(), subject, template, attributes);
        return order;
    }

    @Override
    @Transactional
    public String deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiRequestException("Order not found.", HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
        return "Order deleted successfully";
    }

    @Override
    public DataFetcher<List<Order>> getAllOrdersByQuery() {
        return dataFetchingEnvironment -> orderRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Order>> getUserOrdersByEmailQuery() {
        return dataFetchingEnvironment -> {
            String email = dataFetchingEnvironment.getArgument("email").toString();
            return orderRepository.findOrderByEmail(email);
        };
    }
}