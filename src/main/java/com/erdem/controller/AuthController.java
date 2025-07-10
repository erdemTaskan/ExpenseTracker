package com.erdem.controller;

import com.erdem.dto.JwtResponse;
import com.erdem.dto.LoginRequest;
import com.erdem.dto.RefreshRequest;
import com.erdem.dto.RegisterRequest;
import com.erdem.service.AuthService;
import com.erdem.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

        @PostMapping("/register")
        public ResponseEntity<String> register(@RequestBody RegisterRequest request){
            authService.register(request);
            return ResponseEntity.ok("User registered successfully...");
        }

        @PostMapping("/login")
        public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request){
        JwtResponse jwtResponse=authService.login(request);
        return ResponseEntity.ok(jwtResponse);
        }

        @PostMapping("/refresh")
        public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshRequest request){

        JwtResponse jwtResponse= authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
        }

        @PostMapping("/logout")
        public ResponseEntity<String> logout(@RequestBody RefreshRequest request){

        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Successfully logged out...");
        }
}
