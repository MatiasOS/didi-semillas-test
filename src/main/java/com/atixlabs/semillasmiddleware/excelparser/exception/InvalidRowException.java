package com.atixlabs.semillasmiddleware.excelparser.exception;

public class InvalidRowException extends Exception {

    public InvalidRowException(String errorMessage){
        super(errorMessage);
    }
}
