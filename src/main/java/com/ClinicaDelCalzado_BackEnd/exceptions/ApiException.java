package com.ClinicaDelCalzado_BackEnd.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception containing relevant information for API errors.
 */
public class ApiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7954487639001126933L;

    private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    private final String code;

    private final String description;

    private final Integer statusCode;

    /**
     * Create a new instance, with httpStatus.
     */
    public ApiException() {
        super(DEFAULT_HTTP_STATUS.getReasonPhrase());

        this.code = DEFAULT_HTTP_STATUS.name();
        this.description = DEFAULT_HTTP_STATUS.getReasonPhrase();
        this.statusCode = DEFAULT_HTTP_STATUS.value();
    }

    /**
     * Creates a new instance, with provided fields.
     *
     * @param code API error code.
     * @param description API error description.
     * @param statusCode API error HTTP Status code.
     */
    public ApiException(String code, String description, Integer statusCode) {
        super();
        this.code = code;
        this.description = description;
        this.statusCode = statusCode;
    }

    /**
     * Creates a new instance, with provided fields.
     *
     * @param code API error code.
     * @param description API error description.
     * @param statusCode API error HTTP Status code.
     * @param cause API error cause.
     */
    public ApiException(String code, String description, Integer statusCode, Throwable cause) {
        super();
        this.code = code;
        this.description = description;
        this.statusCode = statusCode;
    }

    /**
     * @return API error code.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @return API error description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return API error HTTP Status code.
     */
    public Integer getStatusCode() {
        return this.statusCode;
    }
}
