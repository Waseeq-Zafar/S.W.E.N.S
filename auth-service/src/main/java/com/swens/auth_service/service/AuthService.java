package com.swens.auth_service.service;

import com.swens.auth_service.dto.LoginRequestDTO;
import com.swens.auth_service.dto.LoginResponseDTO;
import com.swens.auth_service.grpc.UserServiceGrpcClient;
import com.swens.auth_service.util.JwtUtil;
import com.swens.grpc.UserResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserServiceGrpcClient userServiceGrpcClient;

    // In-memory store for refresh tokens
    private final ConcurrentHashMap<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public AuthService(PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UserServiceGrpcClient userServiceGrpcClient) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userServiceGrpcClient = userServiceGrpcClient;
    }

    public Optional<LoginResponseDTO> authenticate(LoginRequestDTO loginRequestDTO) {
        UserResponse grpcUser = userServiceGrpcClient.getUserCredentials(loginRequestDTO.getEmail());

        if (grpcUser == null || grpcUser.getName().isEmpty()) {
            return Optional.empty();
        }

        boolean matches = passwordEncoder.matches(
                loginRequestDTO.getPassword(),
                grpcUser.getHashedPassword()
        );

        if (!matches) {
            return Optional.empty();
        }

        String refreshToken = jwtUtil.generateRefreshToken(loginRequestDTO.getEmail(), grpcUser.getRole());
        String accessToken = jwtUtil.generateJwtToken(loginRequestDTO.getEmail(), grpcUser.getRole());

        // Store refresh token against email
        refreshTokenStore.put(loginRequestDTO.getEmail(), refreshToken);

        return Optional.of(new LoginResponseDTO(grpcUser.getName(), accessToken, refreshToken));
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }

    public Optional<LoginResponseDTO> refreshAccessToken(String refreshToken) {
        try {
            // Verify token signature and expiration
            jwtUtil.validateToken(refreshToken);

            // Extract email and role from a valid token
            String email = jwtUtil.extractEmailFromToken(refreshToken);
            String role = jwtUtil.extractRoleFromToken(refreshToken);

            // Check if a token exists in our store (simple map-based store)
            String storedToken = refreshTokenStore.get(email);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                return Optional.empty(); // Either no token or mismatch
            }

            // Generate a new access token
            String newAccessToken = jwtUtil.generateJwtToken(email, role);
            return Optional.of(new LoginResponseDTO(email, newAccessToken, refreshToken));

        } catch (ExpiredJwtException e) {
            // Remove expired token from store
            String email = jwtUtil.extractEmailFromToken(refreshToken); // can still extract from the expired token
            refreshTokenStore.remove(email);

            // Inform the frontend to redirect to log in
            return Optional.empty();

        } catch (JwtException e) {
            // Invalid token
            return Optional.empty();
        }
    }
}
