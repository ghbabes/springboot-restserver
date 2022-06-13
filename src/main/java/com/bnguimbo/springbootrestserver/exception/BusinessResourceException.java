package com.bnguimbo.springbootrestserver.exception;

import org.springframework.http.HttpStatus;

public class BusinessResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Long resourceId;
	private String errorCode;
	private HttpStatus status;

    public BusinessResourceException(final String message) {
        super(message);
    }

    public BusinessResourceException(final Long resourceId, final String message) {
        super(message);
        this.resourceId = resourceId;
    }
    public BusinessResourceException(final Long resourceId, final String errorCode, final String message) {
    	super(message);
        this.resourceId = resourceId;
        this.errorCode = errorCode;
    }

    public BusinessResourceException(final String errorCode, final String message) {
    	super(message);
        this.errorCode = errorCode;
    }

    public BusinessResourceException(final String errorCode, final String message, final HttpStatus status) {
    	super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(final Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(final String errorCode) {
		this.errorCode = errorCode;
	}

    public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(final HttpStatus status) {
		this.status = status;
	}
}