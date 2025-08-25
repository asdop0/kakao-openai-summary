package com.asd.exception;

public class SummaryAccessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    public SummaryAccessException() {
        super("글이 존재하지 않거나 작성자가 아닙니다.");
    }

    public SummaryAccessException(String message) {
        super(message);
    }
}
