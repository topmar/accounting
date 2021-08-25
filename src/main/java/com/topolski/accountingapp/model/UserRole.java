package com.topolski.accountingapp.model;

public enum UserRole {
    MASTER ("ROLE_MASTER"),
    ADMIN ("ROLE_ADMIN"),
    USER ("ROLE_USER"),
    GUEST ("ROLE_GUEST");
    private final String role;

    UserRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }
}
