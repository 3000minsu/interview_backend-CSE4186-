package CSE4186.interview.exception;


import CSE4186.interview.utils.ApiUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        logger.error(e.getMessage(), e);
        ApiUtil.ApiErrorResult<String> error = ApiUtil.error(statusCode.value(), "알 수 없는 오류 발생");
        return super.handleExceptionInternal(e, error, headers, statusCode, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<?> handleIllegalStateException(Exception e) {
        logger.error(e.getMessage(), e);
        ApiUtil.ApiErrorResult<String> error = ApiUtil.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(error);
    }


    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<?> handleNotFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(ApiUtil.error(HttpServletResponse.SC_NOT_FOUND, e.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(ApiUtil.error(HttpServletResponse.SC_BAD_REQUEST, "Method Argument Not Valid"));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleNormalException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(ApiUtil.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "알 수 없는 오류 발생"));
    }

}
