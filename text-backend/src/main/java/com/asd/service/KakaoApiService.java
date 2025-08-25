package com.asd.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.asd.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoApiService {
	
	@Value("${kakao.key}")
	private String REST_API_KEY;
    private final String REDIRECT_URI = "http://localhost:5173/oauth/callback";

    // 인증 코드 -> 토큰
    @Transactional
    public User getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", REST_API_KEY);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        
        try {
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
	        
	        String accessToken = response.getBody().get("access_token").toString();
	        String refreshToken = response.getBody().get("refresh_token").toString();
	        
	        User user = new User();
	        user.setKakaoAccessToken(accessToken);
	        user.setKakaoRefreshToken(refreshToken);
	
	        return user;
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Invalid or expired code", e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Kakao server error", e);
        } catch (Exception e) {
            throw new RuntimeException("Unknown error during Kakao token request", e);
        }
    }
	
    // 토큰 -> 카카오 아이디
	public Long getKakaoId(String accessToken) {
		String url = "https://kapi.kakao.com/v1/user/access_token_info";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + accessToken);

	    HttpEntity<String> entity = new HttpEntity<>(headers);

	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

	    return Long.valueOf(response.getBody().get("id").toString());
	}
	
	// 로그아웃
	public void logOut(User user) {
	    String url = "https://kapi.kakao.com/v1/user/logout";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + user.getKakaoAccessToken());

	    HttpEntity<String> entity = new HttpEntity<>(headers);

	    RestTemplate restTemplate = new RestTemplate();
	    try {
	        restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
	        // 성공 시 별도 반환값 필요 없음
	    } catch (HttpClientErrorException e) {
	        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
	            // 1. 액세스 토큰 만료
	            // 2. refresh token으로 새 access token 발급
	            String newAccessToken = refreshAccessToken(user.getKakaoRefreshToken());
	            
	            // 카카오 리프레시 토큰 만료로 인한 로그아웃 요청 불가
	            if(newAccessToken == null) {
	            	System.out.println("카카오 리프레시 토큰 만료");
	            	return;
	            }
	            
	            // 3. 새 액세스로 로그아웃 재시도
	            headers.set("Authorization", "Bearer " + newAccessToken);
	            entity = new HttpEntity<>(headers);
	            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

	        } else {
	            throw new RuntimeException("카카오 로그아웃 실패");
	        }
	    } catch (HttpServerErrorException e) {
	        throw new RuntimeException("카카오 서버 오류로 로그아웃 실패");
	    }
	}

	// 액세스 토큰 만료 시 
	public String refreshAccessToken(String refreshToken) {
		String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", REST_API_KEY);
        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        
        try {
	        RestTemplate restTemplate = new RestTemplate();
	        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
	        
	        return response.getBody().get("access_token").toString();
        } catch (HttpClientErrorException e) { // 카카오 리프레시 토큰 만료. 
            return null;
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Kakao server error", e);
        } catch (Exception e) {
            throw new RuntimeException("Unknown error during Kakao token request", e);
        }
	}
}
