package com.ClinicaDelCalzado_BackEnd.services.impl;

import com.ClinicaDelCalzado_BackEnd.dtos.questions.AnswerDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.questions.QuestionDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.AdminDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateAdminDTORequest;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateAdminPasswordDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.request.UpdateAdminQuestionDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AdminDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.AdminListDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.enums.AdminStatusEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.enums.AdminTypeEnum;
import com.ClinicaDelCalzado_BackEnd.dtos.response.UpdateAdminPasswordDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.response.UpdateAdminQuestionDTOResponse;
import com.ClinicaDelCalzado_BackEnd.dtos.userAdmin.AdminDTO;
import com.ClinicaDelCalzado_BackEnd.entity.Administrator;
import com.ClinicaDelCalzado_BackEnd.entity.Answer;
import com.ClinicaDelCalzado_BackEnd.entity.SecurityQuestion;
import com.ClinicaDelCalzado_BackEnd.exceptions.AlreadyExistsException;
import com.ClinicaDelCalzado_BackEnd.exceptions.BadRequestException;
import com.ClinicaDelCalzado_BackEnd.exceptions.UnauthorizedException;
import com.ClinicaDelCalzado_BackEnd.repository.userAdmin.IAdministratorRepository;
import com.ClinicaDelCalzado_BackEnd.services.IAdminService;
import com.ClinicaDelCalzado_BackEnd.services.IAnswerService;
import com.ClinicaDelCalzado_BackEnd.services.IQuestionService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdminServiceImpl implements IAdminService {

    private final IAdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final IAnswerService answerService;
    private final IQuestionService questionService;

    @Autowired
    public AdminServiceImpl(IAdministratorRepository administratorRepository, PasswordEncoder passwordEncoder, IAnswerService answerService, IQuestionService questionService) {
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @Override
    public Optional<Administrator> findAdministratorById(Long idAdministrator) {
        return administratorRepository.findById(idAdministrator);
    }

    @Override
    public AdminDTOResponse findAdministratorByIdAdmin(Long idAdministrator) {
        validateIdentification(idAdministrator);

        AdminDTOResponse adminDTOResponse = new AdminDTOResponse();
        Optional<Administrator> administrator = findAdministratorById(idAdministrator);

        if (administrator.isEmpty()) {
            throw new NotFoundException(String.format("La identificación %s del administrador no existe", idAdministrator));
        }

        adminDTOResponse.setMessage("Detalles del administrador recuperados exitosamente.");
        adminDTOResponse.setAdmin(administrator.map(p ->
                new AdminDTO(p.getIdAdministrator(), AdminTypeEnum.getValue(p.getRole()), p.getAdminName(),
                        p.getAdmPhoneNumber(), AdminStatusEnum.getValue(p.getAdminStatus()), p.getHasTemporaryPassword())).get());

        return adminDTOResponse;

    }

    @Override
    public AdminListDTOResponse findAdministratorAll() {

        List<Administrator> administratorList = administratorRepository.findAll();

        AdminListDTOResponse adminListDTOResponse = new AdminListDTOResponse();
        adminListDTOResponse.setAdmins(administratorList.stream().map(p -> {
                    if (!p.getRole().equalsIgnoreCase(AdminTypeEnum.ADMINISTRATOR.name())) {
                        return new AdminDTO(p.getIdAdministrator(), AdminTypeEnum.getValue(p.getRole()), p.getAdminName(),
                                p.getAdmPhoneNumber(), AdminStatusEnum.getValue(p.getAdminStatus()), p.getHasTemporaryPassword());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList());

        return adminListDTOResponse;
    }

    @Override
    public AdminDTOResponse create(AdminDTORequest adminDTO) {

        validateInputData(adminDTO, true);

        Long idAdministrator = adminDTO.getIdentification();

        if (findAdministratorById(idAdministrator).isPresent()) {
            throw new AlreadyExistsException(
                    String.format("El administrador con identificador %s ya existe", idAdministrator));
        }

        Administrator administrator = buildAdministrator(
                idAdministrator,
                adminDTO.getName(),
                adminDTO.getCellphone(),
                AdminStatusEnum.ACTIVE.getKeyName(),
                AdminTypeEnum.getName(adminDTO.getAdminType()),
                passwordEncoder.encode(adminDTO.getPassword()),
                true);

        saveAdmin(administrator);

        return adminDTOResponse("Administrador creado exitosamente.", administrator);
    }

    @Override
    public AdminDTOResponse update(Long adminId, UpdateAdminDTORequest adminDTO, Long userId) {

        validateInputData(AdminDTORequest.builder()
                .identification(adminId)
                .build(), false);

        Administrator administrator = matchDifferencesAdmin(validateAdminIdExists(adminId), adminDTO, userId);

        saveAdmin(administrator);

        return adminDTOResponse("Administrador actualizado exitosamente.", administrator);
    }

    @Override
    public UpdateAdminPasswordDTOResponse updatePassword(Long adminId, UpdateAdminPasswordDTO adminDTO) {

        validateIdentification(adminId);

        Administrator administrator = validateAdminIdExists(adminId);

        String pwdNewEncode = passwordEncode(adminDTO.getNewPassword());

        validatePassword(adminDTO.getOldPassword(), adminDTO.getNewPassword(), adminDTO.getConfirmNewPassword());

        if (!passwordEncoder.matches(adminDTO.getOldPassword(), administrator.getPassword())) {
            throw new UnauthorizedException("Clave anterior es incorrecta.");
        }

        if (!adminDTO.getNewPassword().equals(adminDTO.getConfirmNewPassword())) {
            throw new BadRequestException("Datos proporcionados son inválidos o las claves nuevas no coinciden.");
        }

        if (adminDTO.getOldPassword().equals(adminDTO.getNewPassword())) {
            throw new BadRequestException("La clave anterior y la nueva no pueden ser iguales.");
        }

        administrator.setPassword(pwdNewEncode);
        administrator.setHasTemporaryPassword(false);

        saveAdmin(administrator);

        return UpdateAdminPasswordDTOResponse.builder()
                .message("Clave cambiada exitosamente.")
                .build();
    }

    @Override
    public UpdateAdminQuestionDTOResponse updateAnswer(Long adminId, UpdateAdminQuestionDTO adminDTO) {

        AdminDTOResponse update = update(adminId, UpdateAdminDTORequest.builder()
                .name(adminDTO.getName())
                .cellphone(adminDTO.getPhone())
                .build(), 0L);

        if (adminDTO.getSecurityQuestions().isEmpty()) {
            throw new BadRequestException("El listado de preguntas y respuestas no puede ser vacío");
        }

        if (adminDTO.getSecurityQuestions().stream().map(AnswerDTO::getIdQuestion).distinct().count() == 1) {
            throw new BadRequestException("Hay preguntas que se repiten por lo que debe seleccionar diferentes tipos de preguntas");
        }

        List<Answer> currentAnswer = answerService.findAnswerAllByAdminId(adminId);

        //Actualizar todos los estados a false
        if (!currentAnswer.isEmpty()) {
            currentAnswer.forEach(
                    p -> {
                        answerService.saveAnswer(Answer.builder()
                                .idAnswers(p.getIdAnswers())
                                .securityQuestion(SecurityQuestion.builder().idSecurityQuestion(p.getSecurityQuestion().getIdSecurityQuestion()).build())
                                .answer(p.getAnswer())
                                .idAdministrator(Administrator.builder().idAdministrator(adminId).build())
                                .status(false)
                                .build());
                    }
            );
        }

        adminDTO.getSecurityQuestions().forEach(p -> {
                    Answer newAnswer = new Answer();
                    currentAnswer.stream()
                            .filter(answer ->
                                    answer.getSecurityQuestion().getIdSecurityQuestion().longValue() == p.getIdQuestion())
                            .map(Answer::getIdAnswers)
                            .forEach(newAnswer::setIdAnswers);
                    newAnswer.setSecurityQuestion(SecurityQuestion.builder().idSecurityQuestion(p.getIdQuestion().intValue()).build());
                    newAnswer.setAnswer(p.getAnswer());
                    newAnswer.setIdAdministrator(Administrator.builder().idAdministrator(adminId).build());
                    newAnswer.setStatus(true);
                    answerService.saveAnswer(newAnswer);
                }
        );

        List<QuestionDTO> questionDTOList = questionService.findAllQuestions().getQuestions();

        List<AnswerDTO> securityQuestions = adminDTO.getSecurityQuestions().stream()
                .map(s -> createAnswerDTO(s, questionDTOList))
                .toList();

        return UpdateAdminQuestionDTOResponse.builder()
                .message("Información personal editada exitosamente.")
                .admin(UpdateAdminQuestionDTO.builder()
                        .name(update.getAdmin().getName())
                        .phone(update.getAdmin().getCellphone())
                        .securityQuestions(securityQuestions)
                        .build())
                .build();

    }

    private Administrator buildAdministrator(Long adminId, String adminName, String adminPhoneNumber,
                                             String adminStatus, String role, String password,
                                             Boolean hasTemporaryPwd) {
        return Administrator.builder()
                .idAdministrator(adminId)
                .adminName(adminName)
                .admPhoneNumber(adminPhoneNumber)
                .adminStatus(adminStatus)
                .role(role)
                .password(password)
                .hasTemporaryPassword(hasTemporaryPwd)
                .build();
    }

    @Override
    public void saveAdmin(Administrator administrator) {
        administratorRepository.save(administrator);
    }

    @Override
    public String passwordEncode(String password) {
        return passwordEncoder.encode(password);
    }

    private AdminDTOResponse adminDTOResponse(String message, Administrator administrator) {
        return AdminDTOResponse.builder()
                .message(message)
                .admin(AdminDTO.builder()
                        .identification(administrator.getIdAdministrator())
                        .adminType(AdminTypeEnum.getValue(administrator.getRole()))
                        .status(AdminStatusEnum.getValue(administrator.getAdminStatus()))
                        .name(administrator.getAdminName())
                        .cellphone(administrator.getAdmPhoneNumber())
                        .hasTemporaryPassword(administrator.getHasTemporaryPassword())
                        .build())
                .build();
    }

    private void validateInputData(AdminDTORequest adminDTO, Boolean validateAllFields) {

        validateIdentification(adminDTO.getIdentification());

        if (ObjectUtils.isEmpty(adminDTO.getName()) && validateAllFields) {
            throw new BadRequestException("El nombre es un campo obligatorio, no puede ser vacío");
        }

        if (ObjectUtils.isEmpty(adminDTO.getAdminType()) && validateAllFields) {
            throw new BadRequestException("El tipo de administrador es un campo obligatorio, no puede ser vacío");
        }

        if (ObjectUtils.isEmpty(adminDTO.getCellphone()) && validateAllFields) {
            throw new BadRequestException("El telefono es un campo obligatorio, no puede ser vacío");
        }

        if (ObjectUtils.isEmpty(adminDTO.getPassword()) && validateAllFields) {
            throw new BadRequestException("La contraseña es un campo obligatorio, no puede ser vacío");
        }
    }

    @Override
    public Administrator validateAdminIdExists(Long id) {
        Optional<Administrator> administratorById = findAdministratorById(id);

        if (administratorById.isEmpty()) {
            throw new NotFoundException(String.format("El administrador con identificación %s no existe", id));
        }
        return administratorById.get();
    }

    private void validateIdentification(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new BadRequestException("La identificación es un campo obligatorio, no puede ser vacio");
        }
    }

    private void validatePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        if (ObjectUtils.isEmpty(oldPassword)) {
            throw new BadRequestException("La contraseña actual no puede ser vacia");
        }

        if (ObjectUtils.isEmpty(newPassword)) {
            throw new BadRequestException("La nueva contraseña no puede ser vacia");
        }

        if (ObjectUtils.isEmpty(confirmNewPassword)) {
            throw new BadRequestException("La confirmación de la nueva contraseña no puede ser vacia");
        }
    }

    private Administrator matchDifferencesAdmin(Administrator currentDataAdmin, UpdateAdminDTORequest newDataAdmin, Long userId) {

        if (currentDataAdmin.getIdAdministrator().equals(userId)) {
            throw new BadRequestException("No se pueden actualizar los datos del mismo usuario autenticado desde el listado, dirijase a editar perfil");
        }

        return buildAdministrator(
                currentDataAdmin.getIdAdministrator(),
                ObjectUtils.isEmpty(newDataAdmin.getName()) || Objects.equals(currentDataAdmin.getAdminName(), newDataAdmin.getName()) ?
                        currentDataAdmin.getAdminName() :
                        newDataAdmin.getName(),
                ObjectUtils.isEmpty(newDataAdmin.getCellphone()) || Objects.equals(currentDataAdmin.getAdmPhoneNumber(), newDataAdmin.getCellphone()) ?
                        currentDataAdmin.getAdmPhoneNumber() :
                        newDataAdmin.getCellphone(),
                ObjectUtils.isEmpty(newDataAdmin.getAdminStatus()) || Objects.equals(AdminStatusEnum.getValue(currentDataAdmin.getAdminStatus()), newDataAdmin.getAdminStatus()) ?
                        currentDataAdmin.getAdminStatus() :
                        AdminStatusEnum.getName(newDataAdmin.getAdminStatus()),
                ObjectUtils.isEmpty(newDataAdmin.getAdminType()) || Objects.equals(AdminTypeEnum.getValue(currentDataAdmin.getRole()), newDataAdmin.getAdminType()) ?
                        currentDataAdmin.getRole() :
                        AdminTypeEnum.getName(newDataAdmin.getAdminType()),
                currentDataAdmin.getPassword(),
                currentDataAdmin.getHasTemporaryPassword());
    }

    private AnswerDTO createAnswerDTO(AnswerDTO s, List<QuestionDTO> questionDTOList) {
        String questionText = questionDTOList.stream()
                .filter(f -> s.getIdQuestion().equals(f.getIdQuestion()))
                .map(QuestionDTO::getQuestion)
                .findFirst()
                .orElse(""); // Devuelve una cadena vacía si no se encuentra la pregunta

        return AnswerDTO.builder()
                .idQuestion(s.getIdQuestion())
                .question(questionText)
                .answer(s.getAnswer())
                .build();
    }


}
