package com.maa.alk.ecommerce.mapper;

import com.maa.alk.ecommerce.domain.Review;
import com.maa.alk.ecommerce.domain.User;
import com.maa.alk.ecommerce.dto.perfume.FullPerfumeResponse;
import com.maa.alk.ecommerce.dto.review.ReviewRequest;
import com.maa.alk.ecommerce.dto.review.ReviewResponse;
import com.maa.alk.ecommerce.dto.user.BaseUserResponse;
import com.maa.alk.ecommerce.dto.user.UpdateUserRequest;
import com.maa.alk.ecommerce.dto.user.UserResponse;
import com.maa.alk.ecommerce.exception.InputFieldException;
import com.maa.alk.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final CommonMapper commonMapper;
    private final UserService userService;

    public UserResponse getUserById(Long userId) {
        return commonMapper.convertToResponse(userService.getUserById(userId), UserResponse.class);
    }

    public UserResponse getUserInfo(String email) {
        return commonMapper.convertToResponse(userService.getUserInfo(email), UserResponse.class);
    }

    public List<FullPerfumeResponse> getCart(List<Long> perfumesIds) {
        return commonMapper.convertToResponseList(userService.getCart(perfumesIds), FullPerfumeResponse.class);
    }

    public List<BaseUserResponse> getAllUsers() {
        return commonMapper.convertToResponseList(userService.getAllUsers(), BaseUserResponse.class);
    }

    public UserResponse updateUserInfo(String email, UpdateUserRequest userRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        User user = commonMapper.convertToEntity(userRequest, User.class);
        return commonMapper.convertToResponse(userService.updateUserInfo(email, user), UserResponse.class);
    }

    public ReviewResponse addReviewToPerfume(ReviewRequest reviewRequest, Long perfumeId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        Review review = commonMapper.convertToEntity(reviewRequest, Review.class);
        return commonMapper.convertToResponse(userService.addReviewToPerfume(review, perfumeId), ReviewResponse.class);
    }
}
