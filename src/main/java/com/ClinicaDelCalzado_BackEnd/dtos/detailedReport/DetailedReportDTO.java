package com.ClinicaDelCalzado_BackEnd.dtos.detailedReport;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DetailedReportDTO {

    private String creationDate;
    private String orderNumber;
    private Long totalServicesValue;
    private Long totalDeposits;
    private Long totalBalance;
    private Long servicesReceived;
    private Long servicesCompleted;
    private Long servicesDispatched;
}
