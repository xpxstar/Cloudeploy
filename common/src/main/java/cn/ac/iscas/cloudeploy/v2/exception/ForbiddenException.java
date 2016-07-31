package cn.ac.iscas.cloudeploy.v2.exception;

public class ForbiddenException extends BusinessException {
	private static final long serialVersionUID = 5346650090152850396L;

	public ForbiddenException() {

	}

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorCode getErrorCode() {
		return ErrorCode.FORBIDDEN;
	}
}
