package cu.cujae.aica.signdigital.controller;

import cu.cujae.aica.signdigital.dto.ErrorResponse;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

public interface IHandlerExceptionController {
    ResponseEntity<ErrorResponse> exceptionPSQLHandler(HttpRequest request, PSQLException e);
    ResponseEntity<ErrorResponse> sqlExceptionHandler(HttpRequest request, SQLException e);
    ResponseEntity<ErrorResponse> nullPointerException(HttpRequest request, NullPointerException e);
    ResponseEntity<ErrorResponse> exceptionHandler(HttpRequest request, Exception e);
}
