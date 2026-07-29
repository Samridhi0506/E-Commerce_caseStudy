package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.SignupRequest;
import com.ecommerce.backend.dto.response.LoginResponse;

public interface AuthService {

    void signup(SignupRequest request);

    LoginResponse login(LoginRequest request);
}