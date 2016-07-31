package cn.ac.iscas.cloudeploy.v2.exception;

public class BadRequestException extends BusinessException {
	private static final long serialVersionUID = 5129662267537316850L;

	public BadRequestException() {

	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequestException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorCode getErrorCode() {
		return ErrorCode.BAD_REQUEST;
	}
}
