package com.ClinicaDelCalzado_BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "operator")
public class Operator {

    @Id
    @Column(name = "id_operator", length = 20)
    private Long idOperator;

    @Column(name = "operator_name", nullable = false, length = 100)
    private String operatorName;

    @Column(name = "ope_phone_number", length = 20)
    private String opePhoneNumber;

    @Column(name = "status_operator", length = 15)
    private String statusOperator;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idOperator")
    private List<ServicesEntity> servicesOpeList;
}
