package com.example.demo.error;

public class UserAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = -5530494540412377098L;
	
	public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistException(final String message) {
        super(message);
    }

    public UserAlreadyExistException(final Throwable cause) {
        super(cause);
    }

}
