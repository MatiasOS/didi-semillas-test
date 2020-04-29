package com.atixlabs.semillasmiddleware.excelparser.app.exception;

public class InvalidQuestionException extends Exception {
    public InvalidQuestionException(String invalidQuestion){
        super("No existe la clave: " + invalidQuestion);
    }
}
