package com.asd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asd.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByKakaoId(Long kakaoId);
	
	Optional<User> findByBackendRefreshToken(String backendRefreshToken);
}
