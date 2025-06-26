package com.swens.auth_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swens.auth_service.dto.LoginRequestDTO;
import com.swens.auth_service.dto.LoginResponseDTO;
import com.swens.auth_service.dto.UserCacheDTO;
import com.swens.auth_service.grpc.UserServiceGrpcClient;
import com.swens.auth_service.util.JwtUtil;
import com.swens.grpc.UserResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserServiceGrpcClient userServiceGrpcClient;

//    private final ConcurrentHashMap<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    public AuthService(PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UserServiceGrpcClient userServiceGrpcClient,
                       StringRedisTemplate redisTemplate,
                       ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userServiceGrpcClient = userServiceGrpcClient;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<LoginResponseDTO> authenticate(LoginRequestDTO loginRequestDTO) {
        UserResponse grpcUser;

        try {
            String email = loginRequestDTO.getEmail();
            String cacheKey = "user:" + email;
            String cachedUserJson = redisTemplate.opsForValue().get(cacheKey);

            UserCacheDTO cachedUser;

            if (cachedUserJson != null) {
                cachedUser = objectMapper.readValue(cachedUserJson, UserCacheDTO.class);
                System.out.println("Loaded from Redis cache");

                grpcUser = UserResponse.newBuilder()
                        .setName(cachedUser.getName())
                        .setHashedPassword(cachedUser.getHashedPassword())
                        .setRole(cachedUser.getRole())
                        .build();

            } else {
                grpcUser = userServiceGrpcClient.getUserCredentials(email);
                System.out.println("Loaded from gRPC");

                if (grpcUser == null || grpcUser.getName().isEmpty()) {
                    return Optional.empty();
                }

                UserCacheDTO userToCache = new UserCacheDTO(
                        email,
                        grpcUser.getName(),
                        grpcUser.getHashedPassword(),
                        grpcUser.getRole()
                );

                String json = objectMapper.writeValueAsString(userToCache);
                redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(10080));
            }

            boolean matches = passwordEncoder.matches(
                    loginRequestDTO.getPassword(),
                    grpcUser.getHashedPassword()
            );

            if (!matches) return Optional.empty();

            String refreshToken = jwtUtil.generateRefreshToken(email, grpcUser.getRole());
            String accessToken = jwtUtil.generateJwtToken(email, grpcUser.getRole());

//            refreshTokenStore.put(loginRequestDTO.getEmail(), refreshToken);


            redisTemplate.opsForValue().set("refresh_token:" + email, refreshToken);

            return Optional.of(new LoginResponseDTO(grpcUser.getName(), accessToken, refreshToken));

        } catch (Exception e) {
            e.printStackTrace(); // Helps to debug what's wrong
            return Optional.empty();
        }
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Optional<LoginResponseDTO> refreshAccessToken(String refreshToken) {
        try {
            // 1. Validate the token (will throw if invalid/expired)
            jwtUtil.validateToken(refreshToken);

            // 2. Extract data from token
            String email = jwtUtil.extractEmailFromToken(refreshToken);
            String role = jwtUtil.extractRoleFromToken(refreshToken);

//            String storedToken = refreshTokenStore.get(email);

            // 3. Get stored token from Redis
            String storedToken = redisTemplate.opsForValue().get("refresh_token:" + email);

            if (storedToken == null) {
                System.out.println("No stored token found in Redis");
                return Optional.empty();
            }

            if (!storedToken.equals(refreshToken)) {
                System.out.println("Refresh token mismatch");
                return Optional.empty();
            }

            // 4. Generate a new access token
            String newAccessToken = jwtUtil.generateJwtToken(email, role);

            return Optional.of(new LoginResponseDTO(email, newAccessToken, refreshToken));

        } catch (ExpiredJwtException e) {
            // Remove expired token
            try {
                String email = jwtUtil.extractEmailFromToken(refreshToken);

//                refreshTokenStore.remove(email);

                redisTemplate.delete("refresh_token:" + email);

            } catch (Exception ignored) {}

            System.out.println("Refresh token expired");
            return Optional.empty();

        } catch (JwtException e) {
            System.out.println("Invalid refresh token: " + e.getMessage());
            return Optional.empty();
        }
    }


    public String getRole(String token) {
        return jwtUtil.extractRoleFromToken(token);
    }

    public String getEmail(String token) {
        return jwtUtil.extractEmailFromToken(token);
    }
}
