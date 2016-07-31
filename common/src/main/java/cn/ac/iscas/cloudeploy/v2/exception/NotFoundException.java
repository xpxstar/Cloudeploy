package cn.ac.iscas.cloudeploy.v2.exception;

public class NotFoundException extends BusinessException {
	private static final long serialVersionUID = -6258596621049184920L;

	public NotFoundException() {

	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorCode getErrorCode() {
		return ErrorCode.NOT_FOUND;
	}
}
