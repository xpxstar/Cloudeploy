package cn.ac.iscas.cloudeploy.v2.controller.exceptionhandler;

import org.apache.commons.lang3.StringUtils;

public class ErrorResponse {
	private Integer code;
	private String message;

	public ErrorResponse(int code) {
		this.code = code;
		this.message = StringUtils.EMPTY;
	}

	public ErrorResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
