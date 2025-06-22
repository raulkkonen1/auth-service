package com.auth.jwt.service;

import com.auth.jwt.dto.NewUserDto;
import com.auth.jwt.dto.RequestDto;
import com.auth.jwt.dto.TokenDto;
import com.auth.jwt.model.AuthUser;
import com.auth.jwt.repository.AuthUserRepository;
import com.auth.jwt.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthUserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;  // Renombrado correctamente

    public String register(NewUserDto dto) {
        AuthUser user = AuthUser.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
        repo.save(user);
        return "Usuario registrado correctamente";
    }

    public TokenDto login(RequestDto dto) {
        AuthUser user = repo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtProvider.createToken(user);
        return new TokenDto(user.getUsername(), token);
    }

    public boolean validateToken(RequestDto dto, String token) {
        return jwtProvider.validate(token, dto);
    }

}
