package cn.ac.iscas.cloudeploy.v2.controller.exceptionhandler;

import java.io.IOException;
import java.util.Iterator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cn.ac.iscas.cloudeploy.v2.exception.BusinessException;
import cn.ac.iscas.cloudeploy.v2.exception.ErrorCode;

import com.google.common.base.Strings;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

	/**
	 * 重写参数验证异常的处理方法
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		StringBuilder sb = new StringBuilder();
		BindingResult bindingResult = ex.getBindingResult();
		Iterator<FieldError> it = bindingResult.getFieldErrors().iterator();
		if (it.hasNext()) {
			FieldError e = it.next();
			sb.append(e.getField() + ":" + e.getDefaultMessage());
		}
		while (it.hasNext()) {
			FieldError e = it.next();
			sb.append(", " + e.getField() + ":" + e.getDefaultMessage());
		}

		ErrorCode errorCode = ErrorCode.BAD_REQUEST;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(),
				sb.toString());
		return handleExceptionInternal(ex, errorResponse, headers,
				HttpStatus.valueOf(errorCode.statusCode), request);
	}

	/**
	 * 业务逻辑异常
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { BusinessException.class })
	public final ResponseEntity<?> handleException(BusinessException ex,
			WebRequest request) {
		ex.printStackTrace();
		ErrorCode errorCode = ex.getErrorCode();
		String message = ex.getMessage();
		if (Strings.isNullOrEmpty(message)) {
			message = ex.getErrorCode().message;
		}
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(),
				message);
		return handleExceptionInternal(ex, errorResponse, null,
				HttpStatus.valueOf(errorCode.statusCode), request);
	}

	/**
	 * 500 - runtime exception
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { RuntimeException.class })
	public final ResponseEntity<?> handleException(RuntimeException ex,
			WebRequest request) {
		ex.printStackTrace();
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(),
				errorCode.message);
		return handleExceptionInternal(ex, errorResponse, null,
				HttpStatus.valueOf(errorCode.statusCode), request);
	}

	/**
	 * 500 - IO exception
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = { IOException.class })
	public final ResponseEntity<?> handleException(IOException ex,
			WebRequest request) {
		ex.printStackTrace();
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(),
				errorCode.message);
		return handleExceptionInternal(ex, errorResponse, null,
				HttpStatus.valueOf(errorCode.statusCode), request);
	}
}
