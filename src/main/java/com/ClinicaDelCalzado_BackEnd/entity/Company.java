package com.ClinicaDelCalzado_BackEnd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_company", length = 20)
    private Integer idCompany;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "nit", length = 20)
    private String nit;

    @Column(name = "address")
    private String address;

    @Column(name = "phones", length = 100)
    private String phones;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCompany")
    private List<WorkOrder> workOrdersList;
}