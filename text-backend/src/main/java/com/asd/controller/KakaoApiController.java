package com.asd.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asd.model.User;
import com.asd.security.JwtTokenProvider;
import com.asd.service.KakaoApiService;
import com.asd.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kakao")
public class KakaoApiController {
	private final KakaoApiService kakaoApiService;
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;
	
	/*
	private final Map<String, Long> usedCodes = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	*/
	
	@PostMapping("/logIn")
	public ResponseEntity<?> logIn(@RequestBody Map<String, String> requestData) {
		String code = requestData.get("code");
		
		/*
		if (usedCodes.containsKey(code)) {
            throw new CodeAlreadyUsedException();
        }	
		usedCodes.put(code, System.currentTimeMillis());
        scheduler.schedule(() -> usedCodes.remove(code), 5, TimeUnit.MINUTES); 
        */
        
        // 인증 코드 -> 토큰
        User user = kakaoApiService.getAccessToken(code);
        
        // 토큰 -> 카카오 아이디
        user.setKakaoId(kakaoApiService.getKakaoId(user.getKakaoAccessToken()));
        
        // 신규 회원 생성 or 새 로그인
        if(userService.getUserByKakaoId(user.getKakaoId()).isEmpty()) {
        	user.setId(userService.createUser(user.getKakaoId(), user.getKakaoAccessToken(), user.getKakaoRefreshToken()).getId());
        } else {
        	user.setId(userService.updateKakaoRefreshToken(user.getKakaoId(), user.getKakaoAccessToken(), user.getKakaoRefreshToken()));
        }

        // JWT 발급 및 저장
        String accessToken = jwtTokenProvider.createBackendAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createBackendRefreshToken(user.getId());
        userService.updateBackendRefreshToken(user.getId(), refreshToken);

        // 클라이언트에 JWT 전달
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        
        return ResponseEntity.ok(response);
	}
	
	@PostMapping("/logOut")
	public ResponseEntity<?> logout(@RequestBody Map<String, String> requestData) {
	    String refreshToken = requestData.get("refreshToken");
	    
	    // 리프레시 토큰으로 유저 조회
	    User user = userService.getUserByBackendRefreshToken(refreshToken);
	    
	    // 유저 정보로 카카오 로그아웃
	    kakaoApiService.logOut(user);
	    
	    // DB에 남아있는 토큰 제거
	    userService.deleteToken(user.getId());
	    
	    return ResponseEntity.ok("로그아웃 성공");
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> updateAccessToken(@RequestBody Map<String, String> requestData) {
		System.out.println("갱신 시도");
		String refreshToken = requestData.get("refreshToken");
		
		User user = userService.getUserByBackendRefreshToken(refreshToken);
		String accessToken = jwtTokenProvider.createBackendAccessToken(user.getId());
		
		Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        
        return ResponseEntity.ok(response);
	}
}
