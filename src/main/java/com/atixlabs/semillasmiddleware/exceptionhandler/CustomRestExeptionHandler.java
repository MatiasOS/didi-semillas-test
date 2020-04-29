package com.atixlabs.semillasmiddleware.exceptionhandler;

import com.atixlabs.semillasmiddleware.exceptionhandler.dto.ApiError;
import com.atixlabs.semillasmiddleware.filemanager.exception.FileManagerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExeptionHandler extends ResponseEntityExceptionHandler {


    /***
     * FILE MANAGER
     */
    @ExceptionHandler(FileManagerException.class)
    protected ResponseEntity<Object> handleFileManagerException(
            FileManagerException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());

    }
}
