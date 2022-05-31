package com.maa.alk.ecommerce.controller;

import com.maa.alk.ecommerce.dto.GraphQLRequest;
import com.maa.alk.ecommerce.dto.order.OrderItemResponse;
import com.maa.alk.ecommerce.dto.order.OrderRequest;
import com.maa.alk.ecommerce.dto.order.OrderResponse;
import com.maa.alk.ecommerce.dto.perfume.FullPerfumeResponse;
import com.maa.alk.ecommerce.dto.review.ReviewRequest;
import com.maa.alk.ecommerce.dto.review.ReviewResponse;
import com.maa.alk.ecommerce.dto.user.UpdateUserRequest;
import com.maa.alk.ecommerce.dto.user.UserResponse;
import com.maa.alk.ecommerce.mapper.OrderMapper;
import com.maa.alk.ecommerce.mapper.UserMapper;
import com.maa.alk.ecommerce.security.UserPrincipal;
import com.maa.alk.ecommerce.service.graphql.GraphQLProvider;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final GraphQLProvider graphQLProvider;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(userMapper.getUserInfo(user.getPhoneNumber()));
    }

    @PutMapping("/edit")
    public ResponseEntity<UserResponse> updateUserInfo(@AuthenticationPrincipal UserPrincipal user,
                                                       @Valid @RequestBody UpdateUserRequest request,
                                                       BindingResult bindingResult) {
        return ResponseEntity.ok(userMapper.updateUserInfo(user.getPhoneNumber(), request, bindingResult));
    }

    @PostMapping("/cart")
    public ResponseEntity<List<FullPerfumeResponse>> getCart(@RequestBody List<Long> perfumesIds) {
        return ResponseEntity.ok(userMapper.getCart(perfumesIds));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderMapper.getOrderById(orderId));
    }

    @GetMapping("/order/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderMapper.getOrderItemsByOrderId(orderId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(orderMapper.getUserOrders(user.getPhoneNumber()));
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> postOrder(@Valid @RequestBody OrderRequest order, BindingResult bindingResult) {
        return ResponseEntity.ok(orderMapper.postOrder(order, bindingResult));
    }

    @PostMapping("/review")
    public ResponseEntity<ReviewResponse> addReviewToPerfume(@Valid @RequestBody ReviewRequest reviewRequest,
                                                             BindingResult bindingResult) {
        ReviewResponse review = userMapper.addReviewToPerfume(reviewRequest, reviewRequest.getPerfumeId(), bindingResult);
        messagingTemplate.convertAndSend("/topic/reviews/" + reviewRequest.getPerfumeId(), review);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/graphql/info")
    public ResponseEntity<ExecutionResult> getUserInfoByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping("/graphql/orders")
    public ResponseEntity<ExecutionResult> getUserOrdersByQuery(@RequestBody GraphQLRequest request) {
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }
}
