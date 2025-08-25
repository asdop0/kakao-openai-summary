package com.asd.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asd.model.User;
import com.asd.repository.UserRepository;
import com.asd.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id가 존재하지 않습니다."));
	}

	public Optional<User> getUserByKakaoId(Long kakaoId) {
		return userRepository.findByKakaoId(kakaoId);
	}
	
	public User getUserByBackendRefreshToken(String refreshToken) {
		return userRepository.findByBackendRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("refreshToken가 존재하지 않습니다."));
	}
	
	@Transactional
    public User createUser(Long kakaoId, String accessToken, String refreshToken) {

        User user = new User();
        user.setKakaoId(kakaoId);
        user.setKakaoAccessToken(accessToken);
        user.setKakaoRefreshToken(refreshToken);

        return userRepository.save(user);
    }
	
	// 액세스 토큰 갱신 (액세스 토큰 만료 시)
	@Transactional
    public void updateKakaoAccessToken(Long kakaoId, String newAccessToken) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 kakaoId가 존재하지 않습니다."));
        user.setKakaoAccessToken(newAccessToken);
    }

    // 액세스 + 리프레시 토큰 갱신 (새 로그인 시)
    @Transactional
    public Long updateKakaoRefreshToken(Long kakaoId, String newAccessToken, String newRefreshToken) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 kakaoId가 존재하지 않습니다."));
        user.setKakaoAccessToken(newAccessToken);
        user.setKakaoRefreshToken(newRefreshToken);
        
        return user.getId();
    }
    
    // 백엔드 리프레시 토큰 갱신 (새 로그인 시)
    @Transactional
    public void updateBackendRefreshToken(Long id, String newRefreshToken) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id가 존재하지 않습니다."));
        user.setBackendRefreshToken(newRefreshToken);
    }
    
    // 로그아웃 시 모든 토큰 제거
    @Transactional
    public void deleteToken(Long id) {
    	User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id가 존재하지 않습니다."));
    	user.setBackendRefreshToken(null);
    	user.setKakaoAccessToken(null);
    	user.setKakaoRefreshToken(null);
    }
}
