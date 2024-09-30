package com.ClinicaDelCalzado_BackEnd.exceptions;

public class AlreadyExistsException extends ApiException {

    public AlreadyExistsException(String msg) {
        super("Conflict", msg, 409);
    }
}
