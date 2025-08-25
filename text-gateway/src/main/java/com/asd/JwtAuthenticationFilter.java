package com.asd;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
//액세스 토큰의 유효성 검사를 하는 필터
    @Value("${jwt.secret}")
    private String secretKey;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }
    
    public static class Config {
        // Configuration properties (if any) can be added here.
    }
    
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("X-AUTH-TOKEN");
            logger.info("[filter] token 값 추출 완료. token : {}", token);
            logger.info("[doFilterInternal] token 값 유효성 체크 시작");
            
            if (token != null) {
                int validationStatus = validateToken(token);
                if (validationStatus == 1) {
                    logger.info("[doFilterInternal] token 값 유효성 체크 완료");
                    try {
                        Claims claims = Jwts.parser()
                                .verifyWith(getSigningKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
                        Number idNum = claims.get("id", Number.class);
                        if (idNum != null) {
                            Long userId = idNum.longValue();
                            logger.info("[filter] 사용자 ID 추출 완료: {}", userId);

                            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                    .header("X-USER-ID", String.valueOf(userId))
                                    .build();
                            exchange = exchange.mutate().request(mutatedRequest).build();
                            logger.info("[filter] 사용자 ID 삽입 완료: {}", userId);
                        }
                    } catch (Exception e) {
                        logger.error("[filter] 사용자 ID 추출 실패: {}", e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                } else if (validationStatus == -1) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401 상태 코드 설정
                    return exchange.getResponse().setComplete(); // 요청을 차단하고 응답을 완료
                } else if (validationStatus == -2) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 403 상태 코드 설정
                    DataBuffer buffer = exchange.getResponse().bufferFactory()
                            .wrap("{\"errorCode\":\"ACCESS_TOKEN_EXPIRED\",\"message\":\"액세스 토큰이 만료되었습니다.\"}".getBytes());
                    return exchange.getResponse().writeWith(Mono.just(buffer)); // 요청을 차단하고 응답을 완료
                }
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 401 상태 코드 설정
                return exchange.getResponse().setComplete(); // 요청을 차단하고 응답을 완료
            }

            return chain.filter(exchange);
        };
    }
    
    public int validateToken(String token) {
        logger.info("[validateToken] 토큰 유효 체크 시작");
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            logger.info("[validateToken] 토큰 유효 체크 완료");
            return 1; // 유효한 토큰의 경우 1 반환
        }catch (ExpiredJwtException e) {
            // 만료된 토큰은 무시하고 서명만 확인
            logger.info("[validateToken] 토큰이 만료되었지만 서명은 유효합니다.", e.getMessage());
            return -2;
        } catch (JwtException e) {
            // 서명 검증 실패 및 기타 JWT 관련 예외
            logger.error("[validateToken] 유효하지 않은 토큰입니다: {}", e.getMessage());
            return -1; // 유효하지 않은 토큰의 경우 -1 반환

        } catch (Exception e) {
            // 그 외 예외
            logger.error("[validateToken] 토큰 검증 중 예상치 못한 오류 발생: {}", e.getMessage());
            return -1;
        }
    }
}
