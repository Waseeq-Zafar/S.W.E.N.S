package com.swens.auth_service.controller;

import com.swens.auth_service.dto.LoginRequestDTO;
import com.swens.auth_service.dto.LoginResponseDTO;
import com.swens.auth_service.dto.RefreshTokenDTO;
import com.swens.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login with email and password.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.authenticate(loginRequestDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    /**
     * Validate access token from the frontend before navigating to protected areas.
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean isValid = authService.validateToken(token);

        return isValid
                ? ResponseEntity.ok("Token is valid")
                : ResponseEntity.status(401).body("Invalid or expired token");
    }

    /**
     * Refresh access token using a refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        return authService.refreshAccessToken(refreshTokenDTO.getRefreshToken())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }
}
