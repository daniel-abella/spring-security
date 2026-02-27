package com.example.demo.controller;

// MELHORIA - Uso de DTOs para não expor entidades JPA diretamente
// MELHORIA - Validação de input com @Valid para segurança contra injeção
// MELHORIA - Controle de acesso: apenas ADMIN pode criar/listar usuários
// MELHORIA - Logging de auditoria para rastreamento de operações críticas
// MELHORIA - Endpoint /me para usuários consultarem próprio perfil

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserMapper userMapper;

    // MELHORIA - @PreAuthorize protege endpoint: apenas ADMIN pode listar todos usuários
    // MELHORIA - Retorna DTOs sem senha para não expor dados sensíveis
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> all() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Admin '{}' requested user list", auth.getName());
        
        return userService.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // MELHORIA - @Valid ativa validação automática de senha forte e formato de username
    // MELHORIA - Apenas ADMIN pode criar novos usuários
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO userRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Admin '{}' creating new user '{}'", auth.getName(), userRequest.getUsername());
        
        User user = userMapper.toEntity(userRequest);
        User saved = userService.save(user);
        
        logger.info("User '{}' created successfully by admin '{}'", saved.getUsername(), auth.getName());
        return ResponseEntity.ok(userMapper.toResponseDTO(saved));
    }

    // MELHORIA - Endpoint para usuário obter próprios dados sem necessidade de role ADMIN
    // MELHORIA - Previne IDOR permitindo apenas acesso aos próprios dados
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("User '{}' requested their own information", auth.getName());
        
        return userService.findByUsername(auth.getName())
                .map(userMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
