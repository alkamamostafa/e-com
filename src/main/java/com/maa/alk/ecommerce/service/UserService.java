package com.maa.alk.ecommerce.service;

import com.maa.alk.ecommerce.domain.Perfume;
import com.maa.alk.ecommerce.domain.Review;
import com.maa.alk.ecommerce.domain.User;
import graphql.schema.DataFetcher;

import java.util.List;

public interface UserService {

    User getUserById(Long userId);

    User getUserInfo(String email);

    List<User> getAllUsers();

    List<Perfume> getCart(List<Long> perfumeIds);

    User updateUserInfo(String email, User user);

    Review addReviewToPerfume(Review review, Long perfumeId);

    DataFetcher<List<User>> getAllUsersByQuery();

    DataFetcher<User> getUserByQuery();
}
