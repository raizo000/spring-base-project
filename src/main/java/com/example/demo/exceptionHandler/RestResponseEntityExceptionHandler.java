package com.example.demo.exceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.example.demo.control.dto.ResponseDTO;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = { Exception.class, Error.class })
  protected ResponseEntity<Object> handleException(
      RuntimeException ex, WebRequest request) {
    String bodyOfResponse = "Internal Server Error";
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setErrorCode(15200);
    responseDTO.setMessage(bodyOfResponse);
    return handleExceptionInternal(ex, responseDTO,
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}
