package com.ClinicaDelCalzado_BackEnd.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    private static final HttpStatus httpStatusUnauthorized = HttpStatus.UNAUTHORIZED;

    public UnauthorizedException(String msg) {
        super(httpStatusUnauthorized.name(), msg, httpStatusUnauthorized.value());
    }
}
