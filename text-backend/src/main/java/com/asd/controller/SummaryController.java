package com.asd.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asd.DTO.SummaryDetailResponse;
import com.asd.DTO.SummaryListResponse;
import com.asd.service.SummaryService;
import com.asd.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summaries")
public class SummaryController {

    private final SummaryService summaryService;
    private final UserService userService;

    // 요약 생성
    @PostMapping
    public void createSummary(@RequestHeader("X-USER-ID") Long userId, @RequestBody Map<String, String> requestData) {
    	System.out.println(requestData.get("originalText"));
        summaryService.createSummary(userService.getUserById(userId), requestData.get("originalText"));
    }

    // 요약 목록 조회
    @GetMapping
    public List<SummaryListResponse> getSummaries(@RequestHeader("X-USER-ID") Long userId) {
        return summaryService.getSummaries(userService.getUserById(userId));
    }

    // 요약 상세 조회
    @GetMapping("/{id}")
    public SummaryDetailResponse getSummary(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long id) {
        return summaryService.getSummary(userService.getUserById(userId), id);
    }

    // 요약 삭제
    @DeleteMapping("/{id}")
    public void deleteSummary(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long id) {
        summaryService.deleteSummary(userService.getUserById(userId), id);
    }
}
