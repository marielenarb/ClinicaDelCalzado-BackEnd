package com.ClinicaDelCalzado_BackEnd.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAuthorizationException implements AuthenticationEntryPoint {

    private static final HttpStatus httpStatusUnauthorized = HttpStatus.UNAUTHORIZED;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String message = authException.getMessage();
        Integer status = httpStatusUnauthorized.value();
        String statusName = httpStatusUnauthorized.name();

        if (message.equalsIgnoreCase("Full authentication is required to access this resource")) {
            status = HttpStatus.FORBIDDEN.value();
            statusName = HttpStatus.FORBIDDEN.name();
            message = "No tiene permiso para realizar esta acción, contacte al administrador principal";
        } else if (message.equalsIgnoreCase("Bad credentials")) {
            message = "Credenciales invalidas, intente nuevamente!!";
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        String jsonResponse = String.format(
                "{\"status\": %d,\"error\": \"%s\", \"message\": \"%s\"}",
                status,
                statusName,
                message
        );

        response.getWriter().write(jsonResponse);
    }
}
