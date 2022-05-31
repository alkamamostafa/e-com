package com.maa.alk.ecommerce.dto.auth;

import com.maa.alk.ecommerce.dto.user.UserResponse;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private UserResponse user;
    private String token;
}
