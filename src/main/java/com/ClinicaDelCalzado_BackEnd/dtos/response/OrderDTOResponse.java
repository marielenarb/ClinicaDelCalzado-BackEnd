package com.ClinicaDelCalzado_BackEnd.dtos.response;

import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ClientDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderDTOResponse {

    private String orderNumber;
    private ClientDTO client;
    private String createDate;
    private String deliveryDate;
    private Long servicesCount;
    private String orderStatus;
    private Long totalValue;
    private Long downPayment;
    private Long balance;
    private String paymentStatus;
}
