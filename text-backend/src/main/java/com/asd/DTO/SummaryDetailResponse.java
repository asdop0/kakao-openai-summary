package com.asd.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryDetailResponse {
	private Long id;
    private String originalText;
    private String summarizedText;
    private LocalDateTime createDate;
}
