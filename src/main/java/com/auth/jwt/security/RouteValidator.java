package com.auth.jwt.security;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteValidator {

    private final List<String> openEndpoints = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/validate"
    );

    public boolean isOpen(String path) {
        return openEndpoints.stream().anyMatch(path::startsWith);
    }
}
