package com.ClinicaDelCalzado_BackEnd.dtos.request;

import com.ClinicaDelCalzado_BackEnd.dtos.questions.AnswerDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateAdminQuestionDTO {

    private String name;
    private String phone;
    private List<AnswerDTO> securityQuestions;

}
