package warehouse_management.com.warehouse_management.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.auth.request.LoginRequest;
import warehouse_management.com.warehouse_management.dto.auth.response.LoginResponse;

import warehouse_management.com.warehouse_management.integration.auth.dto.request.AuthLoginRequest;
import warehouse_management.com.warehouse_management.integration.auth.dto.response.AuthGetInfoResponse;
import warehouse_management.com.warehouse_management.integration.auth.dto.response.AuthGetPermissionResponse;
import warehouse_management.com.warehouse_management.integration.auth.dto.response.AuthLoginResponse;
import warehouse_management.com.warehouse_management.integration.auth.client.AuthIntegrationClient;
import warehouse_management.com.warehouse_management.dto.auth.response.AnaworkToken;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;
import warehouse_management.com.warehouse_management.utils.JsonUtils;
import warehouse_management.com.warehouse_management.utils.JwtUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class AuthService {
    private final AuthIntegrationClient authIntegrationClient;
    private final JwtUtils jwtUtils;


    @Value("${app.jwt.main-token-exp}")
    private int mainTokenExp;

    public AuthService(AuthIntegrationClient authIntegrationClient, JwtUtils jwtUtils) {
        this.authIntegrationClient = authIntegrationClient;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        AuthLoginResponse authLoginResponse = authIntegrationClient.login(buildAuthLoginRequest(loginRequest));
        AuthGetInfoResponse authGetInfoResponse = authIntegrationClient.getInfo(authLoginResponse.getToken());
        AuthGetPermissionResponse authGetPermissionResponse = authIntegrationClient.getPermission(authLoginResponse.getToken());


        return buildLoginResponse(authLoginResponse, authGetInfoResponse, authGetPermissionResponse);
    }

    private AuthLoginRequest buildAuthLoginRequest(LoginRequest loginRequest) {
        AuthLoginRequest authLoginRequest = new AuthLoginRequest();
        authLoginRequest.setUsername(loginRequest.getEmail());
        authLoginRequest.setPassword(loginRequest.getPassword());

        return authLoginRequest;
    }

    private LoginResponse buildLoginResponse(AuthLoginResponse authLoginResponse, AuthGetInfoResponse authGetInfoResponse, AuthGetPermissionResponse authGetPermissionResponse) {
        String decodePayload = jwtUtils.decodePayload(authLoginResponse.getToken());
        AnaworkToken anaworkToken = JsonUtils.parseJsonToDto(decodePayload, AnaworkToken.class);
//        long expirationEpochSeconds = anaworkToken.getExpiration();
//        Date expirationDate = new Date(expirationEpochSeconds * 1000);
        List<String> permissions = authGetPermissionResponse.getData();
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(GeneralUtil.secretKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Khóa bí mật không phải là chuỗi Base64 hợp lệ: " + e.getMessage());
        }
        Key signingKey = Keys.hmacShaKeyFor(keyBytes);

        String tk = Jwts.builder()
                .setSubject(authGetInfoResponse.getUser().getEmail())
                .claim("id", authGetInfoResponse.getUser().getId())
                .claim("permissions", permissions) // nhúng permission vào JWT
                .setIssuedAt(new Date())
                .setExpiration(generateExpiration())
                .signWith(signingKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
        LoginResponse loginResponse = new LoginResponse();
        AuthGetInfoResponse.UserDTO user = authGetInfoResponse.getUser();
        loginResponse.setToken(tk);
        loginResponse.setFullName(user.getFirstName() + " " + user.getLastName());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setPhone(user.getCellPhone());
        loginResponse.setDepartment(user.getDepartment());
        loginResponse.setPosition(user.getPosition());
        loginResponse.setAvatar(user.getAvatar());

        return loginResponse;
    }

    private Date generateExpiration() {
        long nowMillis = System.currentTimeMillis();
        return new Date(nowMillis + mainTokenExp * 1000L);
    }

}
