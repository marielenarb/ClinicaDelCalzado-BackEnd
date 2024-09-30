package com.ClinicaDelCalzado_BackEnd.dtos.request;

import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ClientDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.CompanyDTO;
import com.ClinicaDelCalzado_BackEnd.dtos.workOrders.ServicesDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WorkOrderDTORequest {

    private CompanyDTO company;
    private Long attendedById;
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate deliveryDate;
    private ClientDTO client;
    private List<ServicesDTO> services;
    private String generalComment;
    private Double downPayment;
}
