package com.ticketsystem.config;

import com.ticketsystem.repository.UserRepository;
import com.ticketsystem.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration @EnableWebSecurity @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {

    private JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;

    @Autowired
    public void setJwtAuthFilter(@Lazy JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                // Config module — GET for authenticated, write for ADMIN
                .requestMatchers(HttpMethod.GET, "/api/config/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/config/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/config/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/config/**").hasRole("ADMIN")
                // Ticket endpoints
                .requestMatchers(HttpMethod.GET, "/api/tickets/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/tickets/**").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/tickets/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                // Project endpoints
                .requestMatchers(HttpMethod.GET, "/api/projects/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/projects/**").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasAnyRole("ADMIN", "PROJECT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")
                // Employee endpoints
                .requestMatchers(HttpMethod.GET, "/api/employees/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/employees/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return u -> userRepository.findByUsernameOrEmail(u, u)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + u));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService());
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
