package com.gallery.fineart.mfineart.service.auth;

import com.gallery.fineart.mfineart.dto.auth.AuthenticatedUserDto;
import com.gallery.fineart.mfineart.dto.auth.LoginRequest;
import com.gallery.fineart.mfineart.dto.auth.LoginResponse;
import com.gallery.fineart.mfineart.security.GalleryUserDetails;
import com.gallery.fineart.mfineart.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        GalleryUserDetails userDetails = (GalleryUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails, userDetails.getRole());

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                userDetails.getUsername(),
                userDetails.getRole()
        );
    }

    public AuthenticatedUserDto currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof GalleryUserDetails userDetails)) {
            throw new IllegalArgumentException("No authenticated user");
        }
        return new AuthenticatedUserDto(userDetails.getUsername(), userDetails.getRole());
    }
}
