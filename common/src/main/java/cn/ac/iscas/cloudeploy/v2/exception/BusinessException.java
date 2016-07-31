package cn.ac.iscas.cloudeploy.v2.exception;

public abstract class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 3076403010908729268L;

	public BusinessException() {

	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public abstract ErrorCode getErrorCode();
}
