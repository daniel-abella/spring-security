package com.example.demo.config;

// MELHORIA - Implementação de autenticação JWT stateless substituindo HTTP Basic
// MELHORIA - Configuração de CORS com whitelist de origins permitidos
// MELHORIA - Adição de Security Headers (HSTS, XSS Protection, Content-Type Options)
// MELHORIA - Proteção de endpoints do Actuator com role ADMIN

import com.example.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // MELHORIA - Endpoints de autenticação públicos para permitir login e obtenção de JWT
        // MELHORIA - Proteção de endpoints do Actuator: health público, demais apenas ADMIN
        http.authorizeHttpRequests(auth -> auth
                // allow H2 console and actuator health endpoint
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/health/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/**")).hasRole("ADMIN")
                // allow authentication endpoints
                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                // allow anonymous GET access to product listing and retrieval (matches ProductController @PermitAll)
                .requestMatchers(new AntPathRequestMatcher("/api/products/**", HttpMethod.GET.name())).permitAll()
                .anyRequest().authenticated()
        );

        // MELHORIA - Session management STATELESS para autenticação JWT sem servidor de sessão
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // MELHORIA - Filtro JWT adicionado antes do UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // HTTP Basic for backward compatibility (can be removed if only JWT is used)
        http.httpBasic(Customizer.withDefaults());

        // For API clients (Postman) it's convenient to disable CSRF in stateless JWT setup
        http.csrf(csrf -> csrf.disable());
        
        // MELHORIA - Security Headers para proteger contra XSS, clickjacking e MIME sniffing
        // MELHORIA - HSTS habilitado com 1 ano de cache para forçar HTTPS
        http.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .contentTypeOptions(Customizer.withDefaults())
                .xssProtection(Customizer.withDefaults())
                .httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true)
                        .maxAgeInSeconds(31536000))
        );

        // MELHORIA - CORS habilitado com configuração customizada
        http.cors(Customizer.withDefaults());

        return http.build();
    }

    // MELHORIA - Configuração CORS com whitelist explícita de origins permitidos
    // MELHORIA - Prevenção de ataques cross-origin maliciosos
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // MELHORIA - AuthenticationManager necessário para validar credenciais no login JWT
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
