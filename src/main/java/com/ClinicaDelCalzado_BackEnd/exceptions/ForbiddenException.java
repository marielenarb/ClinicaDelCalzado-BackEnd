package com.ClinicaDelCalzado_BackEnd.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    private static final HttpStatus httpStatusForbidden = HttpStatus.FORBIDDEN;

    public ForbiddenException(String msg) {
        super(httpStatusForbidden.name(), msg, httpStatusForbidden.value());
    }

}
