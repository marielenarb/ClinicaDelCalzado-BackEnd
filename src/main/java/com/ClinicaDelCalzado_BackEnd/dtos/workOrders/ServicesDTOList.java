package com.ClinicaDelCalzado_BackEnd.dtos.workOrders;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServicesDTOList {

    private Integer id;
    private String idOrder;
    private String name;
    private Long price;
    private OperatorServiceDTO operator;
    private String serviceStatus;
}
