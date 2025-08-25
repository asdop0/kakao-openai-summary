package com.asd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.asd.DTO.OpenAiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAiService {

	@Value("${openai.key}")
    private String OPENAI_API_KEY;
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiResponse summarizeText(String text) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        // messages 구성
        Map<String, Object> messageSystem = Map.of(
                "role", "system",
                "content", """
                본문을 2가지로 요약해줘.
                1) 한 줄 요약 (20자 이내)
                2) 자세한 요약 (200자 정도)

                JSON 형식으로 응답해.
                {
                  "shortText": "...",
                  "summarizedText": "..."
                }
                """
        );

        Map<String, Object> messageUser = Map.of(
                "role", "user",
                "content", text
        );

        // 요청 바디
        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini"); // 모델 선택
        body.put("messages", List.of(messageSystem, messageUser));
        body.put("max_tokens", 350);
        body.put("temperature", 0.3);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // 응답 파싱
            Map<String, Object> choice = ((List<Map<String, Object>>) response.getBody().get("choices")).get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");

            String jsonString = message.get("content").toString().trim();

            return objectMapper.readValue(jsonString, OpenAiResponse.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            System.err.println("API 요청 한도 초과: " + e.getMessage());
            throw new RuntimeException("Failed to summarize text", e);
            // 재시도 로직 또는 사용자 알림 처리
        } catch (Exception e) {
            throw new RuntimeException("Failed to summarize text", e);
        }
    }
}
