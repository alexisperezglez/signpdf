package cu.cujae.aica.signdigital.controller;

import cu.cujae.aica.signdigital.dto.ErrorInfo;
import cu.cujae.aica.signdigital.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class HandlerExceptionsController implements IHandlerExceptionController{

    private ErrorResponse getError(HttpStatus httpStatus, String errorMessage, String errorCode,
                                       String requestUri) {
        ErrorResponse error = new ErrorResponse();
        ErrorInfo errorInfo = new ErrorInfo(errorMessage, errorCode, httpStatus.value(), requestUri);
        error.setError(errorInfo);
        return error;
    }

    @Override
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ErrorResponse> exceptionPSQLHandler(HttpRequest request, PSQLException e) {
        ErrorResponse error = getError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), request.getURI().toString());

        log.error(e.toString());
        e.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> sqlExceptionHandler(HttpRequest request, SQLException e) {
        ErrorResponse error = getError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), request.getURI().toString());

        log.error(e.toString());
        e.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> nullPointerException(HttpRequest request, NullPointerException e) {
        ErrorResponse error = getError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), request.getURI().toString());

        log.error(e.toString());
        e.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpRequest request, Exception e) {
        ErrorResponse error = getError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(), request.getURI().toString());

        log.error(e.toString());
        e.printStackTrace();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
