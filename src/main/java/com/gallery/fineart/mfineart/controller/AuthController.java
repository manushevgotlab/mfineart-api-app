package com.gallery.fineart.mfineart.controller;

import com.gallery.fineart.mfineart.dto.auth.AuthenticatedUserDto;
import com.gallery.fineart.mfineart.dto.auth.LoginRequest;
import com.gallery.fineart.mfineart.dto.auth.LoginResponse;
import com.gallery.fineart.mfineart.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDto> me() {
        return ResponseEntity.ok(authService.currentUser());
    }
}
