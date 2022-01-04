package com.subabk.exception;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = -844531957709051644L;

	public CustomException(String message, Throwable t) {
		throw new RuntimeException(message, t);
	}
}
