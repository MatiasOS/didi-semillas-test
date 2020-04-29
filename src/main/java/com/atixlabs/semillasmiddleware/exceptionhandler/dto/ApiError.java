package com.atixlabs.semillasmiddleware.exceptionhandler.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String debugMessage;
    private String message;
    private List<String> errors;


    public ApiError(HttpStatus status,LocalDateTime timestamp, String message, String debugMessage, List<String> errors) {
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
        this.debugMessage = debugMessage;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error) {
        this.status = status;
        this.message = message;
        this.errors = Arrays.asList(error);
    }
}

