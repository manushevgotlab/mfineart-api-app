package com.gallery.fineart.mfineart.dto.auth;

import com.gallery.fineart.mfineart.enumeration.Role;

public class AuthenticatedUserDto {

    private String username;
    private Role role;

    public AuthenticatedUserDto(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
