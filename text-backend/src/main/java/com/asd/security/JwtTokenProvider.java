package com.asd.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    @Value("${springboot.jwt.secret}")
    private String secretKey;
    @Value("${springboot.jwt.access_secret}")
    private String accessSecretKey;
    private final long tokenValidMillisecond = 1000L * 60 * 60;
    
    // 스프링 빈 생성 후 실행(secretKey 값을 Base64형식으로 인코딩하여 저장)
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private SecretKey getAccessKey() {
    	byte[] keyBytes = Decoders.BASE64.decode(accessSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String createBackendAccessToken(Long id) {
        Date now = new Date();
        String token = Jwts.builder().claim("id", id).issuedAt(now)
                .expiration(new Date(now.getTime() + tokenValidMillisecond)).signWith(getAccessKey()).compact();
        return token;
    }
    public String createBackendRefreshToken(Long id) {
        Date now = new Date();
        String token = Jwts.builder().claim("id", id).issuedAt(now)
                .expiration(new Date(now.getTime() + tokenValidMillisecond * 24 * 265)).signWith(getSigningKey()).compact();
        return token;
    }
}