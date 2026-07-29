package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.LoginRequest;
import com.ecommerce.backend.dto.request.SignupRequest;
import com.ecommerce.backend.service.KeycloakService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.beans.factory.annotation.Value;
import com.ecommerce.backend.dto.response.LoginResponse;

import org.springframework.http.MediaType;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.RoleRepository;
import com.ecommerce.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final RestClient restClient;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
private final RoleRepository roleRepository;

public KeycloakServiceImpl(
        RestClient restClient,
        UserRepository userRepository,
        RoleRepository roleRepository
) {
    this.restClient = restClient;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
}

private String getAdminAccessToken() {

    Map<String, Object> response = restClient.post()
            .uri(serverUrl + "/realms/master/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                    "grant_type=password" +
                    "&client_id=admin-cli" +
                    "&username=" + adminUsername +
                    "&password=" + adminPassword
            )
            .retrieve()
            .body(Map.class);

    if (response == null || response.get("access_token") == null) {
        throw new RuntimeException("Failed to obtain admin access token.");
    }

    return response.get("access_token").toString();
}

    @Override
public void registerUser(SignupRequest request) {

    String token = getAdminAccessToken();

    Map<String, Object> user = Map.of(
            "username", request.getUsername(),
            "email", request.getEmail(),
            "enabled", true,
            "credentials", List.of(
                    Map.of(
                            "type", "password",
                            "value", request.getPassword(),
                            "temporary", false
                    )
            )
    );

    ResponseEntity<Void> response = restClient.post()
            .uri(serverUrl + "/admin/realms/" + realm + "/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .body(user)
            .retrieve()
            .toBodilessEntity();

    URI location = response.getHeaders().getLocation();

    if (location == null) {
        throw new RuntimeException("Unable to fetch Keycloak user id.");
    }

    String keycloakId = location.getPath()
            .substring(location.getPath().lastIndexOf("/") + 1);

    Role role = roleRepository.findByRoleName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found."));

    User appUser = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .keycloakId(keycloakId)
            .role(role)
            .tenant(null)
            .build();

    userRepository.save(appUser);
}

    @Override
public LoginResponse login(LoginRequest request) {

    Map<String, Object> response = restClient.post()
            .uri(serverUrl + "/realms/" + realm + "/protocol/openid-connect/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                    "grant_type=password" +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&username=" + request.getUsername() +
                    "&password=" + request.getPassword()
            )
            .retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> {
                throw new RuntimeException("Invalid username or password.");
            })
            .body(Map.class);

    if (response == null || response.get("access_token") == null) {
        throw new RuntimeException("Failed to login.");
    }

    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setAccessToken(response.get("access_token").toString());
    loginResponse.setRefreshToken(response.get("refresh_token").toString());
    loginResponse.setExpiresIn(
            ((Number) response.get("expires_in")).longValue()
    );

    return loginResponse;
}
}