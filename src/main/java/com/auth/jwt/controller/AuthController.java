package com.auth.jwt.controller;

import com.auth.jwt.dto.NewUserDto;
import com.auth.jwt.dto.RequestDto;
import com.auth.jwt.dto.TokenDto;
import com.auth.jwt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody NewUserDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody RequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validate(
            @RequestBody RequestDto dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = authService.validateToken(dto, token);
        return isValid
                ? ResponseEntity.ok("Token válido")
                : ResponseEntity.status(401).body("Token inválido");
    }

}
