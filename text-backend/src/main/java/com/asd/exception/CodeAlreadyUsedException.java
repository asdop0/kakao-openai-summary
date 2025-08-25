package com.asd.exception;

public class CodeAlreadyUsedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    public CodeAlreadyUsedException() {
        super("이 인증 코드는 이미 사용되었습니다.");
    }

    public CodeAlreadyUsedException(String message) {
        super(message);
    }
}