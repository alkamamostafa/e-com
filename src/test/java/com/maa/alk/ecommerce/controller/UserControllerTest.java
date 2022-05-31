package com.maa.alk.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maa.alk.ecommerce.dto.GraphQLRequest;
import com.maa.alk.ecommerce.dto.order.OrderRequest;
import com.maa.alk.ecommerce.dto.review.ReviewRequest;
import com.maa.alk.ecommerce.dto.user.UpdateUserRequest;
import com.maa.alk.ecommerce.security.JwtAuthenticationException;
import com.maa.alk.ecommerce.util.TestConstants;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/sql/create-user-before.sql", "/sql/create-perfumes-before.sql", "/sql/create-orders-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/create-orders-after.sql", "/sql/create-perfumes-after.sql", "/sql/create-user-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void getUserInfo() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_BASIC + "/info")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(TestConstants.FIRST_NAME))
                .andExpect(jsonPath("$.email").value(TestConstants.USER_EMAIL))
                .andExpect(jsonPath("$.roles").value(TestConstants.ROLE_USER));
    }

    @Test
    public void getUserInfoByJwt() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_BASIC + "/info").header("Authorization", TestConstants.JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value(TestConstants.ADMIN_EMAIL))
                .andExpect(jsonPath("$.roles").value(TestConstants.ROLE_ADMIN));
    }

    @Test(expected = JwtAuthenticationException.class)
    public void getUserInfoByJwtExpired() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_BASIC + "/info").header("Authorization", "jwt")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void getUserInfoByQuery() throws Exception {
        GraphQLRequest graphQLRequest = new GraphQLRequest();
        graphQLRequest.setQuery(TestConstants.GRAPHQL_QUERY_USER);

        mockMvc.perform(post(TestConstants.URL_USERS_GRAPHQL + "/info")
                        .content(mapper.writeValueAsString(graphQLRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.id", equalTo(TestConstants.USER_ID)))
                .andExpect(jsonPath("$.data.user.email", equalTo(TestConstants.USER_EMAIL)))
                .andExpect(jsonPath("$.data.user.firstName", equalTo(TestConstants.FIRST_NAME)))
                .andExpect(jsonPath("$.data.user.lastName", equalTo(TestConstants.LAST_NAME)))
                .andExpect(jsonPath("$.data.user.city", equalTo(TestConstants.CITY)))
                .andExpect(jsonPath("$.data.user.address", equalTo(TestConstants.ADDRESS)))
                .andExpect(jsonPath("$.data.user.phoneNumber", equalTo(TestConstants.PHONE_NUMBER)))
                .andExpect(jsonPath("$.data.user.postIndex", equalTo("1234567890")))
                .andExpect(jsonPath("$.data.user.activationCode", equalTo(null)))
                .andExpect(jsonPath("$.data.user.passwordResetCode", equalTo(null)))
                .andExpect(jsonPath("$.data.user.active", equalTo(true)))
                .andExpect(jsonPath("$.data.user.roles[0]", equalTo(TestConstants.ROLE_USER)));
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void updateUserInfo() throws Exception {
        UpdateUserRequest userRequest = new UpdateUserRequest();
        userRequest.setFirstName(TestConstants.USER2_NAME);
        userRequest.setLastName(TestConstants.USER2_NAME);

        mockMvc.perform(put(TestConstants.URL_USERS_BASIC + "/edit")
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value(TestConstants.USER_EMAIL))
                .andExpect(jsonPath("$.firstName").value(TestConstants.USER2_NAME))
                .andExpect(jsonPath("$.lastName").value(TestConstants.USER2_NAME));
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void updateUserInfo_ShouldInputFieldsAreEmpty() throws Exception {
        UpdateUserRequest userRequest = new UpdateUserRequest();

        mockMvc.perform(put(TestConstants.URL_USERS_BASIC + "/edit")
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstNameError", is("First name cannot be empty")))
                .andExpect(jsonPath("$.lastNameError", is("Last name cannot be empty")));
    }

    @Test
    public void getCart() throws Exception {
        List<Long> perfumesIds = new ArrayList<>();
        perfumesIds.add(2L);
        perfumesIds.add(4L);

        mockMvc.perform(post(TestConstants.URL_USERS_BASIC + "/cart")
                        .content(mapper.writeValueAsString(perfumesIds))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].perfumeTitle").isNotEmpty())
                .andExpect(jsonPath("$[*].perfumer").isNotEmpty())
                .andExpect(jsonPath("$[*].year").isNotEmpty())
                .andExpect(jsonPath("$[*].country").isNotEmpty())
                .andExpect(jsonPath("$[*].perfumeGender").isNotEmpty())
                .andExpect(jsonPath("$[*].fragranceTopNotes").isNotEmpty())
                .andExpect(jsonPath("$[*].fragranceMiddleNotes").isNotEmpty())
                .andExpect(jsonPath("$[*].fragranceBaseNotes").isNotEmpty())
                .andExpect(jsonPath("$[*].description").isNotEmpty())
                .andExpect(jsonPath("$[*].filename").isNotEmpty())
                .andExpect(jsonPath("$[*].price").isNotEmpty())
                .andExpect(jsonPath("$[*].volume").isNotEmpty())
                .andExpect(jsonPath("$[*].type").isNotEmpty());
    }

    @Test
    public void getOrderById() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_ORDER + "/111")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(111))
                .andExpect(jsonPath("$.totalPrice").value(TestConstants.TOTAL_PRICE))
                .andExpect(jsonPath("$.date").value("2021-02-06"))
                .andExpect(jsonPath("$.firstName").value(TestConstants.FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TestConstants.LAST_NAME))
                .andExpect(jsonPath("$.city").value(TestConstants.CITY))
                .andExpect(jsonPath("$.address").value(TestConstants.ADDRESS))
                .andExpect(jsonPath("$.email").value(TestConstants.USER_EMAIL))
                .andExpect(jsonPath("$.phoneNumber").value(TestConstants.PHONE_NUMBER))
                .andExpect(jsonPath("$.postIndex").value(TestConstants.POST_INDEX));
    }
    
    @Test
    public void getOrderById_ShouldNotFound() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_ORDER + "/1111")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Order not found."));
    }

    @Test
    public void getOrderItemsByOrderId() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_ORDER + "/111/items")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", hasSize(2)))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].amount").isNotEmpty())
                .andExpect(jsonPath("$[*].quantity").isNotEmpty());
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void getUserOrders() throws Exception {
        mockMvc.perform(get(TestConstants.URL_USERS_BASIC + "/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].totalPrice", Matchers.hasItem(TestConstants.TOTAL_PRICE)))
                .andExpect(jsonPath("$[*].date").isNotEmpty())
                .andExpect(jsonPath("$[*].firstName", Matchers.hasItem(TestConstants.FIRST_NAME)))
                .andExpect(jsonPath("$[*].lastName", Matchers.hasItem(TestConstants.LAST_NAME)))
                .andExpect(jsonPath("$[*].city", Matchers.hasItem(TestConstants.CITY)))
                .andExpect(jsonPath("$[*].address", Matchers.hasItem(TestConstants.ADDRESS)))
                .andExpect(jsonPath("$[*].email", Matchers.hasItem(TestConstants.USER_EMAIL)))
                .andExpect(jsonPath("$[*].phoneNumber", Matchers.hasItem(TestConstants.PHONE_NUMBER)))
                .andExpect(jsonPath("$[*].postIndex", Matchers.hasItem(TestConstants.POST_INDEX)));
    }

    @Test
    @WithUserDetails(TestConstants.USER_EMAIL)
    public void getUserOrdersByQuery() throws Exception {
        GraphQLRequest graphQLRequest = new GraphQLRequest();
        graphQLRequest.setQuery(TestConstants.GRAPHQL_QUERY_ORDERS);

        mockMvc.perform(post(TestConstants.URL_USERS_GRAPHQL + "/orders")
                        .content(mapper.writeValueAsString(graphQLRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orders[*].id").isNotEmpty())
                .andExpect(jsonPath("$.data.orders[*].totalPrice", Matchers.hasItem(TestConstants.TOTAL_PRICE)))
                .andExpect(jsonPath("$.data.orders[*].date").isNotEmpty())
                .andExpect(jsonPath("$.data.orders[*].firstName", Matchers.hasItem(TestConstants.FIRST_NAME)))
                .andExpect(jsonPath("$.data.orders[*].lastName", Matchers.hasItem(TestConstants.LAST_NAME)))
                .andExpect(jsonPath("$.data.orders[*].city", Matchers.hasItem(TestConstants.CITY)))
                .andExpect(jsonPath("$.data.orders[*].address", Matchers.hasItem(TestConstants.ADDRESS)))
                .andExpect(jsonPath("$.data.orders[*].email", Matchers.hasItem(TestConstants.USER_EMAIL)))
                .andExpect(jsonPath("$.data.orders[*].phoneNumber", Matchers.hasItem(TestConstants.PHONE_NUMBER)))
                .andExpect(jsonPath("$.data.orders[*].postIndex", Matchers.hasItem(TestConstants.POST_INDEX)))
                .andExpect(jsonPath("$.data.orders[*].orderItems").isNotEmpty());
    }

    @Test
    public void postOrder() throws Exception {
        Map<Long, Long> perfumesId = new HashMap<>();
        perfumesId.put(2L, 1L);
        perfumesId.put(4L, 1L);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setFirstName(TestConstants.FIRST_NAME);
        orderRequest.setLastName(TestConstants.LAST_NAME);
        orderRequest.setCity(TestConstants.CITY);
        orderRequest.setAddress(TestConstants.ADDRESS);
        orderRequest.setEmail(TestConstants.ORDER_EMAIL);
        orderRequest.setPostIndex(TestConstants.POST_INDEX);
        orderRequest.setPhoneNumber(TestConstants.PHONE_NUMBER);
        orderRequest.setTotalPrice(TestConstants.TOTAL_PRICE);
        orderRequest.setPerfumesId(perfumesId);

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_USERS_ORDER)
                        .content(mapper.writeValueAsString(orderRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(TestConstants.FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TestConstants.LAST_NAME))
                .andExpect(jsonPath("$.city").value(TestConstants.CITY))
                .andExpect(jsonPath("$.address").value(TestConstants.ADDRESS))
                .andExpect(jsonPath("$.email").value(TestConstants.ORDER_EMAIL))
                .andExpect(jsonPath("$.phoneNumber").value(TestConstants.PHONE_NUMBER))
                .andExpect(jsonPath("$.postIndex").value(TestConstants.POST_INDEX))
                .andExpect(jsonPath("$.totalPrice").value(TestConstants.TOTAL_PRICE));
    }

    @Test
    public void postOrder_ShouldInputFieldsAreEmpty() throws Exception {
        OrderRequest OrderRequest = new OrderRequest();

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_USERS_ORDER)
                        .content(mapper.writeValueAsString(OrderRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstNameError", is("Fill in the input field")))
                .andExpect(jsonPath("$.lastNameError", is("Fill in the input field")))
                .andExpect(jsonPath("$.cityError", is("Fill in the input field")))
                .andExpect(jsonPath("$.addressError", is("Fill in the input field")))
                .andExpect(jsonPath("$.emailError", is("Email cannot be empty")))
                .andExpect(jsonPath("$.phoneNumberError", is("Phone number cannot be empty")))
                .andExpect(jsonPath("$.postIndexError", is("Post index cannot be empty")));
    }

    @Test
    public void addReviewToPerfume() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setPerfumeId(1L);
        reviewRequest.setAuthor(TestConstants.FIRST_NAME);
        reviewRequest.setMessage("Hello world");
        reviewRequest.setRating(5);

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_USERS_REVIEW)
                        .content(mapper.writeValueAsString(reviewRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.author", equalTo(TestConstants.FIRST_NAME)))
                .andExpect(jsonPath("$.rating", equalTo(5)))
                .andExpect(jsonPath("$.date", equalTo("2022-05-23")))
                .andExpect(jsonPath("$.message", equalTo("Hello world")));
    }

    @Test
    public void addReviewToPerfume_ShouldNotFound() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setPerfumeId(111L);
        reviewRequest.setAuthor(TestConstants.FIRST_NAME);
        reviewRequest.setMessage("Hello world");
        reviewRequest.setRating(5);

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_USERS_REVIEW)
                        .content(mapper.writeValueAsString(reviewRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", equalTo("Perfume not found.")));
    }

    @Test
    public void addReviewToPerfume_ShouldInputFieldsAreEmpty() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_USERS_REVIEW)
                        .content(mapper.writeValueAsString(reviewRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.authorError", is("Fill in the input field")))
                .andExpect(jsonPath("$.messageError", is("Fill in the input field")));
    }
}
