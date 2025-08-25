package com.asd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asd.model.Summary;

public interface SummaryRepository extends JpaRepository<Summary, Long>{
	
	@Query("SELECT s FROM Summary s JOIN FETCH s.user WHERE s.user.id = :userId")
	List<Summary> findAllWithUserByUserId(@Param("userId") Long userId);
	
	@Query("SELECT s FROM Summary s WHERE s.id = :summaryId AND s.user.id = :userId")
    Optional<Summary> findByIdAndUserId(@Param("summaryId") Long summaryId, @Param("userId") Long userId);
	
	@Modifying
	@Query("DELETE FROM Summary s WHERE s.id = :summaryId AND s.user.id = :userId")
	int deleteByIdAndUserId(@Param("summaryId") Long summaryId, @Param("userId") Long userId);
}
