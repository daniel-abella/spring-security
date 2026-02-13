package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.Profile;
import com.example.demo.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Coletar permissões a partir dos perfis do usuário
        java.util.Set<String> auths = new java.util.HashSet<>();
        if (u.getProfiles() != null) {
            for (Profile p : u.getProfiles()) {
                if (p.getPermissions() != null) {
                    for (Permission perm : p.getPermissions()) {
                        String name = perm.getName();
                        if (name == null || name.isBlank()) continue;
                        // garantir prefixo ROLE_ para compatibilidade com hasRole/@Secured
                        auths.add(name.startsWith("ROLE_") ? name : "ROLE_" + name);
                    }
                }
            }
        }

        String[] authorities = auths.toArray(new String[0]);

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(authorities)
                .build();
    }
}
