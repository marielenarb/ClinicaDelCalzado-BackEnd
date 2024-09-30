package com.ClinicaDelCalzado_BackEnd.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    /**
     * Método para crear un token por medio de la autenticación
     */
    public String generateToken(Authentication authentication) {
        String userName = authentication.getName();
        Date currentTime = new Date();
        Date tokenExpiration = new Date(currentTime.getTime() + SecurityConstants.JWT_EXPIRATION_TOKEN);

        // Linea para generar el token
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(tokenExpiration)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SIGNATURE)
                .compact();
    }

    /**
     * Método para extraer un user apartir de un token
     */
    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.JWT_SIGNATURE)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Método para validar el token
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_SIGNATURE).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("El token ha expirado o esta incorrecto!!");
        }

    }
}
