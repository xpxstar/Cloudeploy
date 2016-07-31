package cn.ac.iscas.cloudeploy.v2.exception;

public class UnauthorizedException extends BusinessException {
	private static final long serialVersionUID = 4002157344714323838L;

	public UnauthorizedException() {

	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorCode getErrorCode() {
		return ErrorCode.UNAUTHORIZED;
	}
}
