package com.ClinicaDelCalzado_BackEnd.dtos.operator;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OperatorDTO {

    private Long idOperator;
    private String operatorName;
    private String opePhoneNumber;
    private String statusOperator;

}
