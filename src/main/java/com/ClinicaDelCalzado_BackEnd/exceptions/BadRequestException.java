package com.ClinicaDelCalzado_BackEnd.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    private static final HttpStatus httpStatusBadRequest = HttpStatus.BAD_REQUEST;

    public BadRequestException(String msg) {
        super(httpStatusBadRequest.name(), msg, httpStatusBadRequest.value());
    }
}
