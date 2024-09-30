package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.enums.AdminStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.login.LoginDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.questions.AnswerDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.PasswordRecoveryDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AuthDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.MessageDTOResponse;
import com.ClinicaDelCalzado_BackEnd.entity.Administrator;
import com.ClinicaDelCalzado_BackEnd.entity.Answer;
import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import com.ClinicaDelCalzado_BackEnd.exceptions.UnauthorizedException;
import com.ClinicaDelCalzado_BackEnd.services.IAdminService;
import com.ClinicaDelCalzado_BackEnd.services.IAnswerService;
import com.ClinicaDelCalzado_BackEnd.services.IAuthService;
import com.ClinicaDelCalzado_BackEnd.util.JWTUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtGenerator;
    private final IAdminService adminService;
    private final IAnswerService answerService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JWTUtil jwtGenerator, IAdminService adminService, IAnswerService answerService) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.adminService = adminService;
        this.answerService = answerService;
    }

    @Override
    public AuthDTOResponse login(LoginDTO loginDTO) {

        Administrator administrator = adminService.validateAdminIdExists(loginDTO.getIdentification());

        if (administrator.getAdminStatus().equalsIgnoreCase(AdminStatusEnum.INACTIVE.getKeyName())) {
            throw new UnauthorizedException("Credenciales invalidas, contacte al administrador principal!");
        }

        if (ObjectUtils.isEmpty(loginDTO.getIdentification())){
            throw new BadRequestException("La identificación no puede ser vacia.");
        }

        if (ObjectUtils.isEmpty(loginDTO.getPassword())){
            throw new BadRequestException("La contraseña no puede ser vacia.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getIdentification(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        return new AuthDTOResponse("Inicio de sesión exitoso!", token);

    }

    @Override
    public MessageDTOResponse passwordRecovery(PasswordRecoveryDTORequest passwordRecoveryDTORequest) {

        Long adminId = passwordRecoveryDTORequest.getIdentification();

        validatePWDRequest(adminId,
                passwordRecoveryDTORequest.getNewPassword(),
                passwordRecoveryDTORequest.getConfirmNewPassword(),
                passwordRecoveryDTORequest.getAnswersSecurity());

        Administrator administrator = adminService.validateAdminIdExists(adminId);
        List<Answer> currentAnswer = answerService.findAnswerAllByAdminId(adminId);

        if(currentAnswer.isEmpty()) {
            throw new BadRequestException("No puede recuperar contraseña hasta que configure las preguntas de seguridad");
        }

        boolean allAnswerMatch = currentAnswer.stream().filter(Answer::getStatus).allMatch(answer ->
                passwordRecoveryDTORequest.getAnswersSecurity().stream().anyMatch(p ->
                        p.getIdQuestion().equals(answer.getSecurityQuestion().getIdSecurityQuestion().longValue()) &&
                                p.getAnswer().equalsIgnoreCase(answer.getAnswer()))
        );

        if (!allAnswerMatch) {
            throw new BadRequestException("Parámetros de solicitud inválidos o respuestas incorrectas");
        }

        String pwdNewEncode = adminService.passwordEncode(passwordRecoveryDTORequest.getNewPassword());

        administrator.setPassword(pwdNewEncode);
        administrator.setHasTemporaryPassword(false);

        adminService.saveAdmin(administrator);

        return MessageDTOResponse.builder().message("Clave recuperada exitosamente").build();
    }

    private void validatePWDRequest(Long id, String newPassword, String confirmNewPassword, List<AnswerDTO> answerDTOList) {

        if (ObjectUtils.isEmpty(id)) {
            throw new BadRequestException("La identificación es un campo obligatorio, no puede ser vacio");
        }

        if (ObjectUtils.isEmpty(newPassword)) {
            throw new BadRequestException("La nueva contraseña no puede ser vacia");
        }

        if (ObjectUtils.isEmpty(confirmNewPassword)) {
            throw new BadRequestException("La confirmación de la nueva contraseña no puede ser vacia");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException("Datos proporcionados son inválidos o las claves nuevas no coinciden.");
        }

        if (answerDTOList.isEmpty() || answerDTOList.stream().anyMatch(p -> ObjectUtils.isEmpty(p.getIdQuestion()) || ObjectUtils.isEmpty(p.getAnswer()))) {
            throw new BadRequestException("Las preguntas y/o respuestas no pueden ser vacias.");
        }

    }


}
