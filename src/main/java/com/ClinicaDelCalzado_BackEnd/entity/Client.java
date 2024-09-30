package com.ClinicaDelCalzado_BackEnd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "client")
public class Client {

    @Id
    @Column(name = "id_client", length = 20)
    private Long idClient;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "cli_phone_number", length = 20)
    private String cliPhoneNumber;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idClient")
    private List<WorkOrder> workOrdersList;

}