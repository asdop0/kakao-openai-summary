package com.asd.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleException(Exception e) {
	    Map<String, String> body = new HashMap<>();
	    body.put("errorType", e.getClass().getSimpleName());
	    body.put("message", e.getMessage() != null ? e.getMessage() : "서버에서 알 수 없는 오류가 발생했습니다.");

	    HttpStatus status;

	    if (e instanceof IllegalArgumentException) {
	        status = HttpStatus.BAD_REQUEST;
	    } else if (e instanceof CodeAlreadyUsedException) {
	        status = HttpStatus.CONFLICT;
	    } else if (e instanceof SummaryAccessException) {
	    	status = HttpStatus.FORBIDDEN;
	    } else {
	        status = HttpStatus.INTERNAL_SERVER_ERROR;
	    }

	    return ResponseEntity.status(status).body(body);
	}
}