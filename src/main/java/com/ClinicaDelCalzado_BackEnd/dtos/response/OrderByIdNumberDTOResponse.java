package com.ClinicaDelCalzado_BackEnd.dtos.response;

import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ClientDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.CommentDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.CompanyDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ServicesDTO;
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
public class OrderByIdNumberDTOResponse {

    private String orderNumber;
    private CompanyDTO company;
    private String attendedBy;
    private String createDate;
    private String orderStatus;
    private String deliveryDate;
    private ClientDTO client;
    private List<ServicesDTO> services;
    private List<CommentDTO> comments;
    private Long downPayment;
    private Long totalValue;
    private Long balance;
    private String paymentStatus;
}
