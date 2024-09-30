package com.ClinicaDelCalzado_BackEnd.dtos.userAdmin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AdminDTO {

    private Long identification;
    private String adminType;
    private String name;
    private String cellphone;
    private String status;
    private Boolean hasTemporaryPassword;
}
