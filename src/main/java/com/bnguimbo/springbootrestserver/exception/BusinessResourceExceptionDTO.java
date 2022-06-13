package com.bnguimbo.springbootrestserver.exception;

import org.springframework.http.HttpStatus;

public class BusinessResourceExceptionDTO {

    private String errorCode;
    private String errorMessage;
	private String requestURL;
	private HttpStatus status;

    public BusinessResourceExceptionDTO() {
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

	public void setRequestURL(final String url) {
		requestURL = url;

	}
	public String getRequestURL(){
		return requestURL;
	}

    public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(final HttpStatus status) {
		this.status = status;
	}
}