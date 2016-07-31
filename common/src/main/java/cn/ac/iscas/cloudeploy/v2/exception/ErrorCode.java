package cn.ac.iscas.cloudeploy.v2.exception;

public class ErrorCode {
	// 400 errors - bad request
	public static final ErrorCode BAD_REQUEST = new ErrorCode(400, 0,
			"bad request");

	// 401 errors - unauthorized
	public static final ErrorCode UNAUTHORIZED = new ErrorCode(401, 0,
			"not authorized");

	// 404 errors - not found
	public static final ErrorCode NOT_FOUND = new ErrorCode(404, 0, "not found");

	// 403 errors - not permitted
	public static final ErrorCode FORBIDDEN = new ErrorCode(403, 0,
			"not permitted");

	// 409 errors - conflict
	public static final ErrorCode CONFLICT = new ErrorCode(409, 0, "conflict");

	// 500 errors - internal server error
	public static final ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, 0,
			"internal server error");

	public final int statusCode;
	public final int subCode;
	public final String message;

	private ErrorCode(int statusCode, int subCode) {
		this(statusCode, subCode, null);
	}

	private ErrorCode(int statusCode, int subCode, String message) {
		this.statusCode = statusCode;
		this.subCode = subCode;
		this.message = message;
	}

	public int getCode() {
		return statusCode * 100 + subCode;
	}
}
