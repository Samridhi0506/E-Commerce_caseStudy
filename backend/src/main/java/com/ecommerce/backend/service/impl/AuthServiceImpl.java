package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.SignupRequest;
import com.ecommerce.backend.dto.response.LoginResponse;
import com.ecommerce.backend.service.AuthService;
import com.ecommerce.backend.service.KeycloakService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final KeycloakService keycloakService;

    public AuthServiceImpl(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Override
    public void signup(SignupRequest request) {
        keycloakService.registerUser(request);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return keycloakService.login(request);
    }
}