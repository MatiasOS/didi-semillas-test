package com.atixlabs.semillasmiddleware.excelparser.app.exception;

public class InvalidAnswerCastException extends Exception {
    public InvalidAnswerCastException(String answer, String type){
        super(String.format("Tipo de dato incorrecto. No se pudo convertir la respuesta: '%s' a %s",answer,type));
    }
}
