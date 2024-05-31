package org.example.project.exceptions.handle;

import jakarta.persistence.EntityExistsException;
import java.util.HashMap;
import org.example.project.exceptions.custom.EntityNotFoundException;
import org.example.project.exceptions.custom.ValidateException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({EntityNotFoundException.class, EntityExistsException.class})
  protected final ResponseEntity<Object> entityExceptionHandler(Exception ex) {
    if (ex instanceof EntityNotFoundException exception) {
      return getResponseEntity(exception.getDetail(), HttpStatus.NOT_FOUND);
    } else if (ex instanceof EntityExistsException exception) {
      return getResponseEntity(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return allExceptionHandler(ex);
  }

  @ExceptionHandler({ValidateException.class})
  protected final ResponseEntity<Object> validateExceptionHandler(Exception ex) {
    if (ex instanceof ValidateException exception) {
      return getResponseEntity(exception.getDetail(), HttpStatus.BAD_REQUEST);
    }

    return allExceptionHandler(ex);
  }

  @ExceptionHandler({DataIntegrityViolationException.class, DbActionExecutionException.class})
  protected final ResponseEntity<Object> sqlExceptionHandler(Exception ex) {
    if (ex instanceof DataIntegrityViolationException exception) {
      return getResponseEntity(exception.getCause().getLocalizedMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return allExceptionHandler(ex);
  }

  @ExceptionHandler
  protected final ResponseEntity<Object> allExceptionHandler(Exception ex) {
    return getResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<Object> getResponseEntity(String message, HttpStatus httpStatus) {
    var body = new HashMap<String, Object>();
    body.put("success", "true");

    if (message != null) {
      body.put("message", message);
    }
    return new ResponseEntity<>(body, httpStatus);
  }
}