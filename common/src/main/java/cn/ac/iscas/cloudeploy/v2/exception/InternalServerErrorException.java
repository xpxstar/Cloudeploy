package cn.ac.iscas.cloudeploy.v2.exception;

public class InternalServerErrorException extends BusinessException {
	private static final long serialVersionUID = -5245673307163185731L;

	public InternalServerErrorException() {

	}

	public InternalServerErrorException(String message) {
		super(message);
	}

	public InternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalServerErrorException(Throwable cause) {
		super(cause);
	}

	@Override
	public ErrorCode getErrorCode() {
		return ErrorCode.INTERNAL_SERVER_ERROR;
	}
}
