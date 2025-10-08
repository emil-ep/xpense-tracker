package com.xperia.xpense_tracker.exception;

import com.xperia.xpense_tracker.models.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.xperia.exception.TrackerException;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(TrackerException.class)
    public ResponseEntity<ErrorResponse> handleTrackerException(TrackerException ex){
        String errorMessage = ex.getMessage();
        LOGGER.error("Handling TrackerException : {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(errorMessage));
    }
}
