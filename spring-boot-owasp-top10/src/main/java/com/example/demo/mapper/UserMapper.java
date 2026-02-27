package com.example.demo.mapper;

// MELHORIA - Mapper para conversão entre Entity e DTO
// MELHORIA - Isola lógica de conversão e evita exposição de entidades
// MELHORIA - toResponseDTO nunca inclui senha na resposta

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.model.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserMapper {
    
    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .profiles(new HashSet<>())
                .build();
    }
    
    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profiles(user.getProfiles())
                .build();
    }
}
