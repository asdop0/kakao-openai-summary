package com.asd.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asd.DTO.OpenAiResponse;
import com.asd.DTO.SummaryDetailResponse;
import com.asd.DTO.SummaryListResponse;
import com.asd.exception.SummaryAccessException;
import com.asd.model.Summary;
import com.asd.model.User;
import com.asd.repository.SummaryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SummaryService {
	
	private final SummaryRepository summaryRepository;
	private final OpenAiService openAiService;
	
	// 요약 생성
	@Transactional
	public void createSummary(User user, String originalText) {
		
		OpenAiResponse dto = openAiService.summarizeText(originalText); // 요약
		
		Summary summary = new Summary();
		summary.setShortText(dto.getShortText());
		summary.setOriginalText(originalText);
		summary.setSummarizedText(dto.getSummarizedText());
		summary.setUser(user);
		
		summaryRepository.save(summary);
	}
	
	// 요약 목록 조회
	public List<SummaryListResponse> getSummaries(User user) {
		List<Summary> summaryList = summaryRepository.findAllWithUserByUserId(user.getId());
		
		return summaryList.stream()
		    .map(s -> SummaryListResponse.builder()
		            .id(s.getId())
		            .shortText(s.getShortText())
		            .createDate(s.getCreateDate())
		            .build())
		    .collect(Collectors.toList());
	}
	
	// 요약 상세 조회
	public SummaryDetailResponse getSummary(User user, Long id) {
		Summary summary = summaryRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new SummaryAccessException());
		
		return SummaryDetailResponse.builder()
				.id(summary.getId())
				.originalText(summary.getOriginalText())
				.summarizedText(summary.getSummarizedText())
				.createDate(summary.getCreateDate())
				.build();
	}
	
	// 요약 삭제
	@Transactional
	public void deleteSummary(User user, Long id) {
		int deleted = summaryRepository.deleteByIdAndUserId(id, user.getId());
		
		if(deleted == 0) {
			throw new SummaryAccessException();
		}
	}
	
	// 요약 생성
	@Transactional
	public Long createSummaryTest(User user, String originalText) {
		
		Summary summary = new Summary();
		summary.setShortText("테스트용 짧은 요약");
		summary.setOriginalText(originalText);
		summary.setSummarizedText("테스트용 자세한 요약");
		summary.setUser(user);
		
		Summary savedSummary = summaryRepository.save(summary);
		
		return savedSummary.getId();
	}
}
