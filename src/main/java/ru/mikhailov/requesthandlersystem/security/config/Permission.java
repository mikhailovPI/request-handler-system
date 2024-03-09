package ru.mikhailov.requesthandlersystem.security.config;

public enum Permission {
    WRITE("user:write"),
    READ("user:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}