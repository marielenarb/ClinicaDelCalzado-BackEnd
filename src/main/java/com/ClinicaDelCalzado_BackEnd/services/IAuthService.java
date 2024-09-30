package com.ClinicaDelCalzado_BackEnd.services;

import com.ClinicaDelCalzado_BackEnd.dtos.login.LoginDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.PasswordRecoveryDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AuthDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.MessageDTOResponse;

public interface IAuthService {

    AuthDTOResponse login(LoginDTO loginDTO);

    MessageDTOResponse passwordRecovery(PasswordRecoveryDTORequest passwordRecoveryDTORequest);
}
