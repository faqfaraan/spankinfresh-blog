package com.spankinfresh.blog.api;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@ControllerAdvice("com.spankinfresh.blog.api")
public class RestValidationErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public HashMap<String, HashMap<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {
        HashMap<String, String> fieldErrorMap = new HashMap<>();
        HashMap<String, HashMap<String, String>> response = new HashMap<>();
        for (FieldError fieldError :
                exception.getBindingResult().getFieldErrors()) {
            fieldErrorMap.put(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }
        response.put("fieldErrors", fieldErrorMap);
        return response;
    }
}
