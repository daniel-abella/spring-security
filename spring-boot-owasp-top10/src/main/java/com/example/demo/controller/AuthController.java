package com.example.demo.controller;

// MELHORIA - Controller dedicado para autenticação JWT
// MELHORIA - Substitui HTTP Basic por tokens JWT stateless
// MELHORIA - Logging de tentativas de login (sucesso e falha) para auditoria
// MELHORIA - Validação de input com @Valid para prevenir injeção

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // MELHORIA - Autenticação via AuthenticationManager com senha criptografada
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            // MELHORIA - Geração de token JWT com expiração configurada
            final String jwt = jwtUtil.generateToken(userDetails);

            logger.info("User '{}' logged in successfully", loginRequest.getUsername());

            return ResponseEntity.ok(LoginResponseDTO.builder()
                    .token(jwt)
                    .type("Bearer")
                    .username(loginRequest.getUsername())
                    .build());
        } catch (BadCredentialsException e) {
            // MELHORIA - Logging de tentativas de login falhadas para detecção de ataques
            logger.warn("Failed login attempt for user '{}'", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
