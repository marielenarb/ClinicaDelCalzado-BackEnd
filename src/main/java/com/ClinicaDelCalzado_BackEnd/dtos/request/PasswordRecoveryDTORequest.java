package com.ClinicaDelCalzado_BackEnd.dtos.request;

import com.ClinicaDelCalzado_BackEnd.dtos.questions.AnswerDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PasswordRecoveryDTORequest {

    private Long identification;
    private List<AnswerDTO> answersSecurity;
    private String newPassword;
    private String confirmNewPassword;

}
