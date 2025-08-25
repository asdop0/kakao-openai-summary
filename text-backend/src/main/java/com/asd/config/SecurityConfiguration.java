package com.asd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable) // UI를 사용하는 것을 기본값으로 가진 시큐리티 설정을 비활성
                .csrf(CsrfConfigurer::disable) // CSRF 보안설정 비활성
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT 토큰인증 방식의 사용으로 세션은 사용하지 않음
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/kakao/**", "/summaries/**", "/summaries").permitAll()
                        .requestMatchers("**exception**").permitAll().anyRequest().authenticated());
        return http.build();
    }
}

