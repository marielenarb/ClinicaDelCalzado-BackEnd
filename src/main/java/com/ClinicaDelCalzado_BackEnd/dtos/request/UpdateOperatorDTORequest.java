package com.ClinicaDelCalzado_BackEnd.dtos.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateOperatorDTORequest {

    private String operatorName;
    private String opePhoneNumber;
    private String operatorStatus;
}
