package ru.mikhailov.requesthandlersystem.security.model;

public enum Permission {
    USER("user:write"),
    OPERATOR("operator:write"),
    ADMIN("admin:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}