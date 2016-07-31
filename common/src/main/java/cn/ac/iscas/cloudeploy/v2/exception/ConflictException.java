package cn.ac.iscas.cloudeploy.v2.exception;

public class ConflictException extends BusinessException {
	private static final long serialVersionUID = 2957430615702787171L;

	public ConflictException() {

	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConflictException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorCode getErrorCode() {
		return ErrorCode.CONFLICT;
	}
}
