package com.ClinicaDelCalzado_BackEnd.exceptions;

/**
 * Class containing relevant information from an API call error.
 * */
public class ApiError {

    private Integer status;

    private String error;

    private String message;

    /**
     * Creates a new instance, with empty fields.
     */
    public ApiError() { }

    /**
     * Creates a new instance, with provided fields.
     *
     * @param status HTTP Status.
     * @param error error short description.
     * @param message full error message.
     */
    public ApiError( Integer status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * @return error short description.
     */
    public String getError() {
        return this.error;
    }

    /**
     * @param error error short description.
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return full error message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message full error message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return HTTP Status.
     */
    public Integer getStatus() {
        return this.status;
    }

    /**
     * @param status HTTP Status.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

}
