package com.maa.alk.ecommerce.dto.auth;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String phoneNumber;
    private String password;
}
