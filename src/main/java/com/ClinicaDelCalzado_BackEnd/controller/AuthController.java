package com.ClinicaDelCalzado_BackEnd.controller;

import com.ClinicaDelCalzado_BackEnd.dtos.login.LoginDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.PasswordRecoveryDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AuthDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.MessageDTOResponse;
import com.ClinicaDelCalzado_BackEnd.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTOResponse> login(@RequestBody LoginDTO login) {
        AuthDTOResponse authResponse = authService.login(login);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<MessageDTOResponse> passwordRecovery(@RequestBody PasswordRecoveryDTORequest passwordRecoveryDTORequest) {
        MessageDTOResponse messageDTOResponse = authService.passwordRecovery(passwordRecoveryDTORequest);
        return new ResponseEntity<>(messageDTOResponse, HttpStatus.OK);
    }

}
